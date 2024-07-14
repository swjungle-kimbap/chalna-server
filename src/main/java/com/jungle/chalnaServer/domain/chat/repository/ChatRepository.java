package com.jungle.chalnaServer.domain.chat.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoomMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ChatRepository {

    private final ObjectMapper objectMapper;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    private final ListOperations<String, Object> listOperations;
    private final ValueOperations<String, Object> valueOperations;

    private static final String ROOM_KEY_PREFIX = "chat:room:";
    private static final String MESSAGE_ID_KEY = "chat:message:id:";
    private final RedisTemplate<String,Object> redisTemplate;

    public void save(ChatMessage chatMessage) {
        listOperations.rightPush(ROOM_KEY_PREFIX + chatMessage.getChatRoomId(), chatMessage);
    }

    public Long getMessageId() {
        return valueOperations.increment(MESSAGE_ID_KEY, 1);
    }

    public void readLastChatMessage(Long chatRoomId){
        String roomKey = ROOM_KEY_PREFIX + chatRoomId;
        long idx = listOperations.size(roomKey)-1;
        ChatMessage message = objectMapper.convertValue(listOperations.index(roomKey,idx),ChatMessage.class);
        listOperations.set(roomKey, idx, message);
    }

    public List<ChatMessage> getMessagesAfterUpdateDate(Long chatRoomId,LocalDateTime joinedAt, LocalDateTime lastLeaveAt, boolean includePrevious) {
        List<ChatMessage> messages = new LinkedList<>();
        String roomKey = ROOM_KEY_PREFIX + chatRoomId;

        // Redis List의 길이 구하기
        Long len = listOperations.size(roomKey);
        List<Object> rawMessages = listOperations.range(roomKey, 0, len - 1);
        int previousMessageCnt = 100;

        for (int i = rawMessages.size() - 1; i >= 0; i--) {
            ChatMessage message = objectMapper.convertValue(rawMessages.get(i), ChatMessage.class);
            if (!message.getCreatedAt().isBefore(joinedAt) && message.getUpdatedAt().isAfter(lastLeaveAt)) {
                message.read();
                listOperations.set(roomKey, i, message);
                messages.add(0, message);
            } else {
                if (includePrevious && previousMessageCnt > 0) {
                    messages.add(0, message);
                    previousMessageCnt--;
                    continue;
                }
                break;
            }
        }

        return messages;
    }


    public ChatMessage getLatestMessage(Long chatRoomId,ChatRoomMember chatRoomMember) {
        String roomKey = ROOM_KEY_PREFIX + chatRoomId;

        long len = listOperations.size(roomKey);

        for (long i = len - 1; i >= 0; i--) {
            ChatMessage message = objectMapper.convertValue(listOperations.index(roomKey, i), ChatMessage.class);
            if (isChat(message)) {
                if(message.getCreatedAt().isBefore(chatRoomMember.getJoinedAt()))
                    return null;
                return message;
            }
        }
        return null;
    }

    // redis에 변수 만들어서 저장, 읽기 할 때 마다 count하는 방식으로 수정 필요.
    public int getUnreadCount(Long chatRoomId, LocalDateTime lastLeaveAt) {

        int unreadCount = 0;
        String roomKey = ROOM_KEY_PREFIX + chatRoomId;
        Long len = listOperations.size(roomKey);

        List<Object> rawMessages = listOperations.range(roomKey, 0, len - 1);

        for (int i = rawMessages.size() - 1; i >= 0; i--) {
            ChatMessage message = objectMapper.convertValue(rawMessages.get(i), ChatMessage.class);
            if (message.getCreatedAt().isAfter(lastLeaveAt)) {
                if (isChat(message))
                    unreadCount++;
            } else {
                break;
            }
        }
        return unreadCount;
    }

    public boolean isChat(ChatMessage message) {
        ChatMessage.MessageType type = message.getType();
        return ChatMessage.MessageType.CHAT_TYPES.contains(type);
    }

    public void removeChatRoom(Long chatRoomId) {
        String roomKey = ROOM_KEY_PREFIX + chatRoomId;
        redisTemplate.delete(roomKey);
    }

}

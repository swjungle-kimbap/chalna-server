package com.jungle.chalnaServer.domain.chat.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Log4j2
public class ChatRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ROOM_KEY_PREFIX = "chat:room:";
    private static final String MESSAGE_ID_KEY = "chat:message:id:";

    public void saveMessage(ChatMessage chatMessage) {
        redisTemplate.opsForList().rightPush(ROOM_KEY_PREFIX + chatMessage.getChatRoomId(), chatMessage);
    }

    public Long makeMessageId() {
        return redisTemplate.opsForValue().increment(MESSAGE_ID_KEY, 1);
    }

    public List<ChatMessage> getMessagesAfterUpdateDate(Long memberId, Long chatRoomId, LocalDateTime updateDate) {
        List<ChatMessage> messages = new ArrayList<>();
        String roomKey = ROOM_KEY_PREFIX + chatRoomId;
        LocalDateTime now = LocalDateTime.now();

        // Redis List의 길이 구하기
        Long listSize = redisTemplate.opsForList().size(roomKey);
        if( listSize != null && listSize > 0 ) {
            List<Object> rawMessages = redisTemplate.opsForList().range(roomKey, 0, listSize-1);

            for (int i = 0; i < rawMessages.size(); i++) {
                Object rawMessage = rawMessages.get(i);
                ChatMessage message = objectMapper.convertValue(rawMessage, ChatMessage.class);

                if (message.getUpdatedAt() != null && message.getUpdatedAt().isAfter(updateDate)) {
                    if (!message.getSenderId().equals(memberId)) {
                        message.setStatus(true);
                        message.setUpdatedAt(now);
                        redisTemplate.opsForList().set(roomKey, i, objectMapper.convertValue(message, Object.class));
                    }

                    messages.add(message);
                }
            }
        }
        return messages;
    }


    public ChatMessage getLatestMessage(Long chatRoomId) {
        long listSize = redisTemplate.opsForList().size(ROOM_KEY_PREFIX + chatRoomId);

        if (listSize == 0) {
            return null;
        }

        for (long i = listSize - 1; i >= 0; i--) {
            Object rawMessage = redisTemplate.opsForList().index(ROOM_KEY_PREFIX + chatRoomId, i);
            ChatMessage message = objectMapper.convertValue(rawMessage, ChatMessage.class);
            if (message.getType() == ChatMessage.MessageType.CHAT || message.getType() == ChatMessage.MessageType.FRIEND_REQUEST) {
                return message;
            }
        }
        return null;


//        Object rawMeaage = redisTemplate.opsForList().index(ROOM_KEY_PREFIX + chatRoomId, 0);
//        ChatMessage message = objectMapper.convertValue(rawMeaage, ChatMessage.class);
//        return message;
    }

}

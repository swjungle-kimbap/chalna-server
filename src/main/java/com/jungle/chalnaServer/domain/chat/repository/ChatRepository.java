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
        redisTemplate.opsForList().leftPush(ROOM_KEY_PREFIX + chatMessage.getChatRoomId(), chatMessage);
    }

    public Long makeMessageId() {
        return redisTemplate.opsForValue().increment(MESSAGE_ID_KEY, 1);
    }

    public List<ChatMessage> getMessagesAfterUpdateDate(Long chatRoomId, LocalDateTime updateDate) {
        List<ChatMessage> messages = new ArrayList<>();
        String roomKey = ROOM_KEY_PREFIX + chatRoomId;

        // Redis List의 길이 구하기
        Long listSize = redisTemplate.opsForList().size(roomKey);
        if( listSize != null && listSize > 0 ) {
            List<Object> rawMessages = redisTemplate.opsForList().range(roomKey, 0, listSize-1);

            for(Object rawMessage : rawMessages) {
                ChatMessage message = objectMapper.convertValue(rawMessage, ChatMessage.class);
                if (message.getUpdatedAt() != null && message.getUpdatedAt().isAfter(updateDate)) {
                    message.setStatus(false);
                    message.setUpdatedAt(LocalDateTime.now());
                    messages.add(message);
                }
            }
        }
        return messages;
    }


    public ChatMessage getLatestMessage(Long chatRoomId) {
        Object rawMeaage = redisTemplate.opsForList().index(ROOM_KEY_PREFIX + chatRoomId, 0);
        ChatMessage message = objectMapper.convertValue(rawMeaage, ChatMessage.class);
        return message;
    }

}

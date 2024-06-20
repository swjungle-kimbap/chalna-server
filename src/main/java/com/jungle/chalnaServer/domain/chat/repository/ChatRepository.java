package com.jungle.chalnaServer.domain.chat.repository;

import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ROOM_KEY_PREFIX = "chat:room:";
    private static final String MESSAGE_ID_KEY = "chat:message:id:";

    public void saveMessage(ChatMessage chatMessage) {
        redisTemplate.opsForList().leftPush(ROOM_KEY_PREFIX + chatMessage.getChatRoomId(), chatMessage);
    }

    public Long makeMessageId() {
        return redisTemplate.opsForValue().increment(MESSAGE_ID_KEY, 1);
    }
}

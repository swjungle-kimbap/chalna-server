package com.jungle.chalnaServer.domain.chat.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;

import java.time.LocalDateTime;

public class ChatMessageResponse {
    public record MESSAGE<T>(Long id, ChatMessage.MessageType type, T content, Long senderId, Integer unreadCount,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt) {
        public static MESSAGE of(ChatMessage chatMessage) {
            return new MESSAGE<>(chatMessage.getId(),
                    chatMessage.getType(),
                    chatMessage.getContent(),
                    chatMessage.getSenderId(),
                    chatMessage.getUnreadCount(),
                    chatMessage.getCreatedAt());
        }
    }
}

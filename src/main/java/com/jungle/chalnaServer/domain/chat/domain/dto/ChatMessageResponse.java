package com.jungle.chalnaServer.domain.chat.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageResponse {
    Long id;
    ChatMessage.MessageType type;
    String content;
    Long senderId;
    Integer unreadCount;
//    Boolean status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt;

    public ChatMessageResponse(Long id, ChatMessage.MessageType type, String content, Long senderId, Integer unreadCount,LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.senderId = senderId;
        this.unreadCount = unreadCount;
        this.createdAt = createdAt;
    }

    public ChatMessageResponse(ChatMessage chatMessage) {
        this.id = chatMessage.getId();
        this.type = chatMessage.getType();
        this.content = chatMessage.getContent();
        this.senderId = chatMessage.getSenderId();
        this.unreadCount = chatMessage.getUnreadCount();
        this.createdAt = chatMessage.getCreatedAt();
    }
}

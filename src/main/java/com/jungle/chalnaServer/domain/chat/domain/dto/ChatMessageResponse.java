package com.jungle.chalnaServer.domain.chat.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageResponse {
    Long id;
    ChatMessage.MessageType type;
    String content;
    Integer senderId;
    LocalDateTime createdAt;
}

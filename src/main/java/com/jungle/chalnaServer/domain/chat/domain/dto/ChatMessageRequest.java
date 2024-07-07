package com.jungle.chalnaServer.domain.chat.domain.dto;

import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;


public class ChatMessageRequest {
    public record SEND(ChatMessage.MessageType type, String content) {
    }
}

package com.jungle.chalnaServer.domain.chat.domain.dto;

import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import lombok.Getter;

@Getter
public class ChatMessageRequest {
    ChatMessage.MessageType type;
    String content;
}

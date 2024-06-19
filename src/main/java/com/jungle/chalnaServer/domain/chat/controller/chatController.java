package com.jungle.chalnaServer.domain.chat.controller;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageRequest;
import com.jungle.chalnaServer.domain.chat.service.ChatService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@Log4j2
public class chatController {
    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessageRequest requestMessage) {
        chatService.sendMessage(roomId, requestMessage);
    }
}

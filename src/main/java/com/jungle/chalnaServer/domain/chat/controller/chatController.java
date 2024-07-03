package com.jungle.chalnaServer.domain.chat.controller;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageRequest;
import com.jungle.chalnaServer.domain.chat.service.ChatService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@Log4j2
public class chatController {
    @Autowired
    private ChatService chatService;

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessageRequest.SEND requestMessage, SimpMessageHeaderAccessor headerAccessor) {
        Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");
        chatService.sendMessage(memberId, Long.parseLong(roomId), requestMessage);
    }
}

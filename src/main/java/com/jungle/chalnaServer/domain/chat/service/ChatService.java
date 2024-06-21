package com.jungle.chalnaServer.domain.chat.service;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageRequest;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.handler.WebSocketEventListener;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Log4j2
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WebSocketEventListener webSocketEventListener;


    public void sendMessage(String roomId, ChatMessageRequest requestMessage) {
        Long id = chatRepository.makeMessageId();
        Integer senderId = 1; // 임시 사용자 ID, 추후 사용자 정보 가져와서 수정해야 함.
        LocalDateTime now = LocalDateTime.now();
        Boolean status = true;
        // push 알림 보내기. 채팅룸에 멤버 정보를 확인해서 다른 멤버가 채팅방에 없는 경우 알림 보내기
        if (webSocketEventListener.getConnectedCount(roomId) == 1){
            log.info("send push message");
            status = false;
            // push 메시지 보내기
        }


        // 메시지 전달
        ChatMessageResponse responseMessage = ChatMessageResponse.builder()
                .id(id)
                .content(requestMessage.getContent())
                .type(requestMessage.getType())
                .senderId(senderId)
                .createdAt(now)
                .status(status)
                .build();

        messagingTemplate.convertAndSend("/topic/" + roomId, responseMessage);

        // Redis에 메시지 저장
        ChatMessage message = new ChatMessage(id, requestMessage.getType(), senderId,
                Integer.parseInt(roomId), requestMessage.getContent(),  status,
                now, now);

        chatRepository.saveMessage(message);
    }
}

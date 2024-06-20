package com.jungle.chalnaServer.domain.chat.handler;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Log4j2
public class WebSocketEventListener {

    private final Map<String, Set<String>> chatRoomSubscriptions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionChatRoomMap = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatRepository chatRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();
        String chatRoomId = headerAccessor.getFirstNativeHeader("chatRoomId");
        chatRoomSubscriptions.computeIfAbsent(chatRoomId, k -> new CopyOnWriteArraySet<>()).add(sessionId);
        sessionChatRoomMap.put(sessionId, chatRoomId);

        // 이 방에 사용자가 입장했다는 걸 알려주기.
        ChatMessageResponse chatMessage = ChatMessageResponse.builder()
                .id(chatRepository.makeMessageId())
                .senderId(1) // 임시 사용자 정보.
                .type(ChatMessage.MessageType.USER_ENTER)
                .build();


        messagingTemplate.convertAndSend("/topic/" + chatRoomId, chatMessage);

        System.out.println("session Connected : " + sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();
        String chatRoomId = sessionChatRoomMap.get(sessionId);
        Set<String> sessions = chatRoomSubscriptions.get(chatRoomId);

        sessionChatRoomMap.remove(sessionId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                chatRoomSubscriptions.remove(chatRoomId);
            }
        }

        System.out.println("session Disconnected : " + sessionId);

    }

    public Integer getConnectedCount(String chatRoomId) {
        return chatRoomSubscriptions.getOrDefault(chatRoomId, new CopyOnWriteArraySet<>()).size();
    }

}

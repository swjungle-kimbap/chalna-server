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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Log4j2
public class WebSocketEventListener {

//    private final Map<String, Set<String>> chatRoomSubscriptions = new ConcurrentHashMap<>();
//    private final Map<String, String> sessionChatRoomMap = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatRepository chatRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        log.info("connectListener");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();
        String chatRoomId = headerAccessor.getFirstNativeHeader("chatRoomId");
//        chatRoomSubscriptions.computeIfAbsent(chatRoomId, k -> new CopyOnWriteArraySet<>()).add(sessionId);
//        sessionChatRoomMap.put(sessionId, chatRoomId);

        Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");
        Long messageId = 0L;
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));


        // 이 방에 사용자가 입장했다는 걸 알려주기.
        ChatMessageResponse chatMessage = ChatMessageResponse.builder()
                .id(messageId)
                .content("다른 사용자가 입장했습니다.")
                .senderId(memberId)
                .type(ChatMessage.MessageType.USER_ENTER)
                .unreadCount(0)
                .createdAt(now)
                .build();


        messagingTemplate.convertAndSend("/api/sub/" + chatRoomId, chatMessage);

//        ChatMessage message = new ChatMessage(messageId, ChatMessage.MessageType.USER_ENTER, memberId,
//                Long.parseLong(chatRoomId), "다른 사용자가 입장했습니다.", true,
//                now, now);
//
//        chatRepository.saveMessage(message);

        System.out.println("session Connected : " + sessionId);
    }

//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String sessionId = headerAccessor.getSessionId();
//        String chatRoomId = sessionChatRoomMap.get(sessionId);
//        Set<String> sessions = chatRoomSubscriptions.get(chatRoomId);

//        sessionChatRoomMap.remove(sessionId);
//        if (sessions != null) {
//            sessions.remove(sessionId);
//            if (sessions.isEmpty()) {
//                chatRoomSubscriptions.remove(chatRoomId);
//            }
//        }

//        System.out.println("session Disconnected : " + sessionId);
//
//    }

//    public Integer getConnectedCount(Long chatRoomId) {
//        return chatRoomSubscriptions.getOrDefault(chatRoomId.toString(), new CopyOnWriteArraySet<>()).size();
//    }

}

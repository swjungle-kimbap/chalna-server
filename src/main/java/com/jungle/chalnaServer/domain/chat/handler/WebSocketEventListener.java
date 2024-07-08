package com.jungle.chalnaServer.domain.chat.handler;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatRoomMemberResponse;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chat.exception.ChatRoomMemberNotFoundException;
import com.jungle.chalnaServer.domain.chat.repository.ChatRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Log4j2
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        log.info("connectListener");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();
        String chatRoomId = headerAccessor.getFirstNativeHeader("chatRoomId");
        Long memberId = Long.parseLong(headerAccessor.getSessionAttributes().get("memberId").toString());

        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberIdAndChatRoomId(memberId, Long.valueOf(chatRoomId))
                .orElseThrow(ChatRoomMemberNotFoundException::new);

        Long messageId = 0L;
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 이 방에 사용자가 입장했다는 걸 알려주기.
        ChatMessageResponse.MESSAGE<ChatRoomMemberResponse.INFO> res = new ChatMessageResponse.MESSAGE<>(
                messageId
                , ChatMessage.MessageType.USER_ENTER
                , ChatRoomMemberResponse.INFO.of(chatRoomMember)
                , 0L
                , 0
                , now
        );

        messagingTemplate.convertAndSend("/api/sub/" + chatRoomId, res);
    }

}

package com.jungle.chalnaServer.domain.chat.handler;

import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chat.exception.ChatRoomMemberNotFoundException;
import com.jungle.chalnaServer.domain.chat.repository.ChatRoomMemberRepository;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.global.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {
    private final JwtService jwtUtil;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final SetOperations<String, Object> setOperations;

    private final String OFFLINE_KEY_PREFIX = "chat:room:offline:";
    private final MemberRepository memberRepository;

    // websocket을 통해 들어온 요청이 처리 되기전 실행됨
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("Stomphandler preSend!!!!!!!!!!");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        // 연결 요청시 JWT 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("Stomphandler connect !!!!!!!!!!!");
            // Authorization 헤더 추출
            List<String> authorization = accessor.getNativeHeader("Authorization");
            if (authorization != null && !authorization.isEmpty()) {
                String jwt = authorization.get(0).substring(7);

                try {
                    // JWT 토큰 검증
                    if (!jwtUtil.isValidateToken(jwt) || jwtUtil.isExpired(jwt))
                        throw new Exception("jwt is invalid");
                    Long id = jwtUtil.getId(jwt);

                    if (!memberRepository.existsById(id)) {
                        throw new Exception("jwt is invalid");
                    }
                    if (accessor.getFirstNativeHeader("chatRoomId") == null)
                        throw new Exception("chatRoomId is invalid");
                    Long chatRoomId = Long.valueOf(accessor.getFirstNativeHeader("chatRoomId"));
                    if (!chatRoomMemberRepository.existsByMemberIdAndChatRoomId(id, Long.valueOf(chatRoomId)))
                        throw new Exception("jwt is invalid");


                    accessor.getSessionAttributes().put("memberId", id);
                    accessor.getSessionAttributes().put("chatRoomId", chatRoomId);

                    setMemberOnline(chatRoomId, id);


                } catch (Exception e) {
                    log.error("An unexpected error occurred: " + e.getMessage());

                    throw new MessageDeliveryException("UNAUTHORIZED");
                }

            } else {
                // 클라이언트 측 타임아웃 처리
                log.error("Authorization header is not found");
                return null;
            }
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            Long chatRoomId = Long.parseLong(accessor.getSessionAttributes().get("chatRoomId").toString());
            Long memberId = Long.parseLong(accessor.getSessionAttributes().get("memberId").toString());
            setMemberOffline(chatRoomId, memberId);
        }
        return message;
    }

    public void setMemberOnline(Long chatRoomId, Long memberId) {
        log.info("{} online (in {})", memberId, chatRoomId);
        setOperations.remove(OFFLINE_KEY_PREFIX + chatRoomId, memberId.toString());
    }

    public void setMemberOffline(Long chatRoomId, Long memberId) {
        log.info("{} offline (in {})", memberId, chatRoomId);
        ChatRoomMember member = chatRoomMemberRepository.findByMemberIdAndChatRoomId(memberId, chatRoomId)
                .orElseThrow(ChatRoomMemberNotFoundException::new);
        member.updateLastLeaveAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        chatRoomMemberRepository.save(member);
        setOperations.add(OFFLINE_KEY_PREFIX + chatRoomId, memberId.toString());
    }

    public Integer getOfflineMemberCount(Long chatRoomId) {
        return setOperations.size(OFFLINE_KEY_PREFIX + chatRoomId).intValue();
    }

    public Set<Long> getOfflineMembers(Long chatRoomId) {
        return setOperations.members(OFFLINE_KEY_PREFIX + chatRoomId).stream()
                .map(member -> Long.parseLong(member.toString()))
                .collect(Collectors.toSet());
    }

}

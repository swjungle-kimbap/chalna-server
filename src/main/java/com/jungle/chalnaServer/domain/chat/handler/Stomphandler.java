package com.jungle.chalnaServer.domain.chat.handler;

import com.jungle.chalnaServer.domain.member.domain.dto.MemberInfo;
import com.jungle.chalnaServer.domain.member.service.MemberService;
import com.jungle.chalnaServer.global.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class Stomphandler implements ChannelInterceptor {
    private final JwtService jwtUtil;
    private final MemberService memberService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행됨
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("Stomphandler preSend!!!!!!!!!!");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // 연결 요청시 JWT 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("Stomphandler connect !!!!!!!!!!!");
            // Authorization 헤더 추출
            List<String> authorization = accessor.getNativeHeader("Authorization");
            if (authorization != null && !authorization.isEmpty()) {
                String jwt = authorization.get(0).substring(7);

                try {
                    // JWT 토큰 검증
                    if(!jwtUtil.isValidateToken(jwt)){
                        log.info("Stomphandler jwt is invalid");
                        throw new Exception("jwt is invalid");
                    }

                    if(jwtUtil.isExpired(jwt)){
                        log.info("Stomphandler jwt is expired");
                        throw new Exception("jwt is expired");
                    }
                    Long id = jwtUtil.getId(jwt);
                    MemberInfo memberInfo = memberService.getMemberInfo(id);
                    // 사용자 정보 조회
                    accessor.getSessionAttributes().put("memberId", id);
                    accessor.getSessionAttributes().put("username", memberInfo.username());
                } catch (Exception e) {
                    log.error("An unexpected error occurred: " + e.getMessage());

                    throw new MessageDeliveryException("UNAUTHORIZED");
                }

            } else {
                // 클라이언트 측 타임아웃 처리
                log.error("Authorization header is not found");
                return null;
            }
        }
        return message;
    }

}

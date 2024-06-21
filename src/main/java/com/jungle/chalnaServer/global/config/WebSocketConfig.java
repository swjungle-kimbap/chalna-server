package com.jungle.chalnaServer.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { // stomp end point 등록
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*");
//                .withSockJS(); //apic 테스트를 위해 주석처리. 실제 클라이언트와 연결시 주석해제 필요
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");  // Client에서 SEND 요청을 처리
        registry.enableSimpleBroker("/topic");  // SimpleBroker의 기능과 외부 Message Broker에 메세지를 전달하는 기능
    }
}
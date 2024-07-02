package com.jungle.chalnaServer.global.config;

import com.jungle.chalnaServer.domain.chat.handler.Stomphandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final Stomphandler stomphandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { // stomp end point 등록
        registry.addEndpoint("/api/ws")
                .setAllowedOrigins("*")
                .withSockJS(); //apic 테스트를 위해 주석처리. 실제 클라이언트와 연결시 주석해제 필요
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/api/send");   //app  Client에서 SEND 요청을 처리
        registry.enableSimpleBroker("/api/sub")  //topic SimpleBroker의 기능과 외부 Message Broker에 메세지를 전달하는 기능
                .setTaskScheduler(heartBeatScheduler())
                .setHeartbeatValue(new long[] {10000, 10000});
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stomphandler);
    }

    @Bean
    public ThreadPoolTaskScheduler heartBeatScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("websocket-heartbeat-thread-");
        return taskScheduler;
    }

}

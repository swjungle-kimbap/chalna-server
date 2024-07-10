package com.jungle.chalnaServer.domain.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

//@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long id;
    private MessageType type;
    private Long senderId;
    private Long chatRoomId;
    private Object content;
    private Integer unreadCount;


    @CreatedDate
    private LocalDateTime createdAt;


    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void read(){
        this.unreadCount--;
        this.updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public enum MessageType {
        FRIEND_REQUEST, // 친구 요청
        FILE,
        CHAT, // 그냥 메시지,
        TIMEOUT, // 5분 타임아웃 알림
        USER_ENTER, // 다른 사용자 입장
        USER_JOIN,
        USER_LEAVE;
        public static Set<MessageType> CHAT_TYPES = Set.of(FRIEND_REQUEST, FILE, CHAT,TIMEOUT);
    }
}

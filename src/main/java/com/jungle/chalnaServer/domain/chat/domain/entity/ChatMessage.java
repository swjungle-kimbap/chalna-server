package com.jungle.chalnaServer.domain.chat.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

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


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void read(){
        this.unreadCount--;
    }

    public enum MessageType {
        CHAT, // 그냥 메시지
        USER_ENTER, // 다른 사용자 입장
        FRIEND_REQUEST, // 친구 요청
        FILE,
        TIMEOUT // 5분 타임아웃 알림
    }
}

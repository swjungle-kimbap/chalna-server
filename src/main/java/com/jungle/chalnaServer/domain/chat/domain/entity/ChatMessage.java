package com.jungle.chalnaServer.domain.chat.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Builder
@Getter
public class ChatMessage{
    private Long id;
    private MessageType type;
    private Integer senderId;
    private Integer chatRoomId;
    private String content;
    private Boolean status; // 읽음 여부(True: 안읽음, False: 읽음)

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum MessageType {
        CHAT, // 그냥 메시지
        USER_ENTER, // 다른 사용자 입장
        FRIEND_REQUEST // 친구 요청
    }
}

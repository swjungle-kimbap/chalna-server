package com.jungle.chalnaServer.domain.chat.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

//@Builder
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    private Long id;
    private MessageType type;
    private Integer senderId;
    private Integer chatRoomId;
    private String content;
    private Boolean status; // 읽음 여부(True: 안읽음, False: 읽음)

//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public ChatMessage(Long id, MessageType type, Integer senderId, Integer chatRoomId, String content, Boolean status,LocalDateTime createdAt, LocalDateTime updatedAt){
        this.id = id;
        this.type = type;
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

//    @JsonCreator
//    public ChatMessage(@JsonProperty("id") Long id, @JsonProperty("type") MessageType type,
//                       @JsonProperty("senderId") Integer senderId, @JsonProperty("chatRoomId") Integer chatRoomId,
//                       @JsonProperty("content") String content, @JsonProperty("status") Boolean status){
//        this.id = id;
//        this.type = type;
//        this.senderId = senderId;
//        this.chatRoomId = chatRoomId;
//        this.content = content;
//        this.status = status;
//    }

    public enum MessageType {
        CHAT, // 그냥 메시지
        USER_ENTER, // 다른 사용자 입장
        FRIEND_REQUEST // 친구 요청
    }
}

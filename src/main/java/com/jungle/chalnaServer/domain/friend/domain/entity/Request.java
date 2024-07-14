package com.jungle.chalnaServer.domain.friend.domain.entity;

import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Request extends BaseTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long receiverId;

    private Long chatRoomId;

    private String username;

    public Request(Long senderId, Long receiverId, Long chatRoomId, String username) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.chatRoomId = chatRoomId;
        this.username = username;
    }

}

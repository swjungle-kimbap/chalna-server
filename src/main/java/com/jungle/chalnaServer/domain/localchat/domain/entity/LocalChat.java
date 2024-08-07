package com.jungle.chalnaServer.domain.localchat.domain.entity;

import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocalChat extends BaseTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long ownerId;

    @Column(nullable = false)
    private String name;

    private String description;

    private Long imageId;

    @OneToOne
    @JoinColumn(name = "chatRoomId")
    private ChatRoom chatRoom;

    private Double latitude;

    private Double longitude;

    public LocalChat(Long ownerId,String name,String description,Long imageId,ChatRoom chatRoom,Double latitude, Double longitude) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.imageId = imageId;
        this.chatRoom = chatRoom;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

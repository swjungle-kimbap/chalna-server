package com.jungle.chalnaServer.domain.chatRoom.domain.entity;

import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoomMember extends BaseTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    public ChatRoomMember(Long memberId, ChatRoom chatRoom) {
        this.memberId = memberId;
        this.chatRoom = chatRoom;
    }
}

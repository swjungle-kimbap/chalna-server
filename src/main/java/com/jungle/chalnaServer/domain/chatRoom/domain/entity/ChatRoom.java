package com.jungle.chalnaServer.domain.chatRoom.domain.entity;

import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
public class ChatRoom extends BaseTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType type;

    @Column(nullable = false)
    private Integer memberCount;

    private LocalDateTime removedAt;

    @OneToMany(mappedBy = "chatRoom")
    private Set<ChatRoomMember> members;

    public enum ChatRoomType{
        MATCH,
        FRIEND,
        WAITING  // 채팅방 열린지 5분 지난 후. 친구 수락/거절 대기 상태
    }

    public ChatRoom(ChatRoomType type, Integer memberCount) {
        this.type = type;
        this.memberCount = memberCount;
    }

    public void updateType(ChatRoomType newType){
        this.type = newType;
    }

    public void updateMemberCount(Integer newCount) {
        this.memberCount = newCount;
    }

    public void updateRemovedAt() {
        this.removedAt = LocalDateTime.now();
    }

}

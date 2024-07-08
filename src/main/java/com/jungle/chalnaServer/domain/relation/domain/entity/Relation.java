package com.jungle.chalnaServer.domain.relation.domain.entity;

import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@NoArgsConstructor
public class Relation extends BaseTimestampEntity {
    @EmbeddedId
    private RelationPK relationPK;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus friendStatus = FriendStatus.NOTHING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroomId")
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private boolean isBlocked = false;

    @Column(nullable = false)
    private Integer overlapCount = 0;

    private LocalDateTime lastOverlapAt;

    public Relation(RelationPK relationPK){
        this.relationPK = relationPK;
    }
    public void increaseOverlapCount(){
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        if(this.lastOverlapAt == null || now.isAfter(this.lastOverlapAt.plusHours(4))) {
            this.overlapCount += 1;
            this.lastOverlapAt = now;
        }
    }

    public void updateFriendStatus(FriendStatus friendStatus) {
        this.friendStatus = friendStatus;
    }

    public void updateIsBlocked(boolean isBlocked){
        this.isBlocked = isBlocked;
    }

    public void updateChatRoom(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }

}

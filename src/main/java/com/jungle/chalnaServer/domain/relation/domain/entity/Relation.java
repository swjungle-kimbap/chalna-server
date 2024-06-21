package com.jungle.chalnaServer.domain.relation.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Relation {
    @EmbeddedId
    private RelationPK relationPK;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus friendStatus = FriendStatus.PENDING;

    @Column(nullable = false)
    private boolean isBlocked = false;

    @Column(nullable = false)
    private Integer overlapCount = 0;

    private LocalDateTime overlapCountTimestamp;



    public void increaseOverlapCount(){
        this.overlapCount += 1;
        this.overlapCountTimestamp = LocalDateTime.now();
    }
}

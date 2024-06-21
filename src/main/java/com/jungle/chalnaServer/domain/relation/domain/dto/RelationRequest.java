package com.jungle.chalnaServer.domain.relation.domain.dto;

import com.jungle.chalnaServer.domain.relation.domain.entity.FriendStatus;
import com.jungle.chalnaServer.domain.relation.domain.entity.RelationPK;

import java.time.LocalDateTime;

public class RelationRequest {
    private Long id;

    private Long otherId;

    private FriendStatus friendStatus = FriendStatus.PENDING;

    private boolean isBlocked = false;

    private Integer overlapCount = 0;

    private LocalDateTime overlapCountTimestamp;
}

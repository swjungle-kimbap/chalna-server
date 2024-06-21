package com.jungle.chalnaServer.domain.relation.domain.dto;

import com.jungle.chalnaServer.domain.relation.domain.entity.FriendStatus;
import com.jungle.chalnaServer.domain.relation.domain.entity.Relation;

public record RelationResponse(Long id, Long otherId, FriendStatus friendStatus, boolean isBlocked,
                               Integer overlapCount) {
    public static RelationResponse of(Relation relation) {
        return new RelationResponse(
                relation.getRelationPK().getId()
                , relation.getRelationPK().getOtherId()
                , relation.getFriendStatus()
                , relation.isBlocked()
                , relation.getOverlapCount()
        );
    }
}

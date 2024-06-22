package com.jungle.chalnaServer.domain.friend.service;

import com.jungle.chalnaServer.domain.member.domain.dto.MemberResponse;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.domain.relation.domain.entity.FriendStatus;
import com.jungle.chalnaServer.domain.relation.domain.entity.QRelation;
import com.jungle.chalnaServer.domain.relation.repository.RelationRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final MemberRepository memberRepository;
    private final RelationRepository relationRepository;


    public List<MemberResponse> findFriends(Long id){
        return getMemberList(getFriendIdList(id));
    }


    private List<Long> getFriendIdList(Long id){
        QRelation relation = QRelation.relation;
        BooleanExpression expression = relation.relationPK.id.eq(id)
                .and(relation.friendStatus.eq(FriendStatus.ACCEPTED))
                .and(relation.isBlocked.eq(false));

        List<Long> friendIds = new ArrayList<>();
        relationRepository.findAll(expression).forEach((r-> friendIds.add(r.getRelationPK().getOtherId())));
        return friendIds;
    }

    private List<MemberResponse> getMemberList(List<Long> ids){
        return memberRepository.findAllById(ids).stream().map(MemberResponse::of).collect(Collectors.toList());
    }
}

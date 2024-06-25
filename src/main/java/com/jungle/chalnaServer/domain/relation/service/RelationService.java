package com.jungle.chalnaServer.domain.relation.service;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.domain.relation.domain.dto.RelationResponse;
import com.jungle.chalnaServer.domain.relation.domain.entity.FriendStatus;
import com.jungle.chalnaServer.domain.relation.domain.entity.Relation;
import com.jungle.chalnaServer.domain.relation.domain.entity.RelationPK;
import com.jungle.chalnaServer.domain.relation.exception.RelationIdInvalidException;
import com.jungle.chalnaServer.domain.relation.repository.RelationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RelationService {
    private final RelationRepository relationRepository;
    private final MemberRepository memberRepository;

    public RelationResponse findByOtherId(final Long id, final Long otherId) {
        return RelationResponse.of(findRelation(new RelationPK(id, otherId)));
    }

    public RelationResponse findAndIncreaseOverlap(final Long id, final String deviceId) {
        Member member = memberRepository.findByDeviceId(deviceId).orElseThrow(MemberNotFoundException::new);
        RelationPK pk = new RelationPK(id, member.getId());

        Relation relation = findRelation(pk);
        Relation reverse = findRelation(pk.reverse());
        relation.increaseOverlapCount();
        reverse.increaseOverlapCount();
        return RelationResponse.of(relation);
    }
    public String friendUnblock(final Long id,final Long otherId){
        RelationPK pk = new RelationPK(id, otherId);
        Relation relation = findRelation(pk);

        relation.updateIsBlocked(false);
        return "요청에 성공했습니다.";
    }
    public String friendBlock(final Long id,final Long otherId){
        RelationPK pk = new RelationPK(id, otherId);
        Relation relation = findRelation(pk);

        relation.updateFriendStatus(FriendStatus.NOTHING);
        relation.updateIsBlocked(true);
        return "요청에 성공했습니다.";
    }

    public String friendRemove(final Long id,final Long otherId){
        RelationPK pk = new RelationPK(id, otherId);
        Relation relation = findRelation(pk);

        relation.updateFriendStatus(FriendStatus.NOTHING);
        return "요청에 성공했습니다.";
    }

    public String friendAccept(final Long id, final Long otherId) {
        RelationPK pk = new RelationPK(id, otherId);
        Relation relation = findRelation(pk);
        Relation reverse = findRelation(pk.reverse());


        if (relation.getFriendStatus() == FriendStatus.PENDING && reverse.getFriendStatus() != FriendStatus.ACCEPTED) {
            relation.updateFriendStatus(FriendStatus.ACCEPTED);
            reverse.updateFriendStatus(FriendStatus.ACCEPTED);
            return "요청에 성공했습니다.";
        } else {
            return "이미 친구거나, 요청하지 않은 상대입니다.";
        }
    }
    public String friendReject(final Long id, final Long otherId) {
        RelationPK pk = new RelationPK(id, otherId);
        Relation relation = findRelation(pk);
        Relation reverse = findRelation(pk.reverse());


        if (relation.getFriendStatus() == FriendStatus.PENDING && reverse.getFriendStatus() != FriendStatus.ACCEPTED) {
            relation.updateFriendStatus(FriendStatus.NOTHING);
            return "요청에 성공했습니다.";
        } else {
            return "이미 친구거나, 요청하지 않은 상대입니다.";
        }
    }

    public String friendRequest(final Long id, final Long otherId){
        RelationPK pk = new RelationPK(id, otherId);
        Relation relation = findRelation(pk);
        Relation reverse = findRelation(pk.reverse());


        if(relation.getFriendStatus() != FriendStatus.ACCEPTED && reverse.getFriendStatus() == FriendStatus.NOTHING){
            reverse.updateFriendStatus(FriendStatus.PENDING);
            return "요청에 성공했습니다.";
        }
        else{
            return "이미 친구거나, 요청한 상태입니다.";
        }
    }


    public Relation findRelation(RelationPK pk) {
        if (pk.getId().equals(pk.getOtherId()))
            throw new RelationIdInvalidException();
        if (!memberRepository.existsById(pk.getOtherId()))
            throw new MemberNotFoundException();
        Optional<Relation> findRelation = relationRepository.findById(pk);
        return findRelation.orElseGet(() -> createRelation(pk));
    }

    private Relation createRelation(RelationPK pk) {
        relationRepository.save(new Relation(pk.reverse()));
        return relationRepository.save(new Relation(pk));
    }


}

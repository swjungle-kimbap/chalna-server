package com.jungle.chalnaServer.domain.relation.service;

import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.domain.relation.domain.dto.RelationResponse;
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

    public RelationResponse findAndIncreaseOverlap(final Long id, final Long otherId) {
        RelationPK pk = new RelationPK(id, otherId);

        Relation relation = findRelation(pk);
        Relation reverse = findRelation(pk.reverse());
        relation.increaseOverlapCount();
        reverse.increaseOverlapCount();
        return RelationResponse.of(relation);
    }

    private Relation findRelation(RelationPK pk) {
        if (pk.getId().equals(pk.getOtherId()))
            throw new RelationIdInvalidException();
        if (!memberRepository.existsById(pk.getOtherId()))
            throw new MemberNotFoundException();
        Optional<Relation> findRelation = relationRepository.findById(pk);
        if (findRelation.isPresent())
            return findRelation.get();
        else
            return createRelation(pk);
    }

    private Relation createRelation(RelationPK pk) {
        log.info("만듬");
        relationRepository.save(new Relation(pk.reverse()));
        return relationRepository.save(new Relation(pk));
    }


}

package com.jungle.chalnaServer.domain.relation.service;

import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.domain.relation.domain.dto.RelationResponse;
import com.jungle.chalnaServer.domain.relation.domain.entity.Relation;
import com.jungle.chalnaServer.domain.relation.domain.entity.RelationPK;
import com.jungle.chalnaServer.domain.relation.exception.RelationIdInvalidException;
import com.jungle.chalnaServer.domain.relation.repository.RelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RelationService {
    private final RelationRepository relationRepository;
    private final MemberRepository memberRepository;

    public RelationResponse findByOtherId(final Long id,final Long otherId){
        return RelationResponse.of(findAndCreateRelationByOtherId(id, otherId));
    }

    private Relation findAndCreateRelationByOtherId(final Long id,final Long otherId){
        if(id.equals(otherId))
            throw new RelationIdInvalidException();
        if(!memberRepository.existsById(otherId))
            throw new MemberNotFoundException();
        RelationPK pk = new RelationPK(id, otherId);
        Optional<Relation> findRelation = relationRepository.findById(pk);
        return relationRepository.save(findRelation.orElse(new Relation(pk)));
    }


}

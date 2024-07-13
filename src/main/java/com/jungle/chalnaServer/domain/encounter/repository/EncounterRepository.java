package com.jungle.chalnaServer.domain.encounter.repository;

import com.jungle.chalnaServer.domain.encounter.domain.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {

    List<Encounter> findByMemberIdAndOtherId(Long memberId, Long otherId);

}

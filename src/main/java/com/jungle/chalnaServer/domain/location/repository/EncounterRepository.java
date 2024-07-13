package com.jungle.chalnaServer.domain.location.repository;

import com.jungle.chalnaServer.domain.location.domain.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {

    List<Encounter> findByMemberIdAndOtherId(Long memberId, Long otherId);

}

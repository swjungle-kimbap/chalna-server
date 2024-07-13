package com.jungle.chalnaServer.domain.encounter.service;

import com.jungle.chalnaServer.domain.encounter.domain.dto.EncounterResponse;
import com.jungle.chalnaServer.domain.encounter.domain.entity.Encounter;
import com.jungle.chalnaServer.domain.encounter.repository.EncounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EncounterService {

    private final EncounterRepository encounterRepository;

    public List<EncounterResponse.LOCATION> getLocation(Long memberId, Long otherId) {
        List<Encounter> locations = encounterRepository.findByMemberIdAndOtherId(memberId, otherId);
        return  locations.stream()
                .map(EncounterResponse::of)
                .collect(Collectors.toList());
    }
}

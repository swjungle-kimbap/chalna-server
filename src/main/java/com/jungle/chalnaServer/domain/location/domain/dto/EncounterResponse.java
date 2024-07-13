package com.jungle.chalnaServer.domain.location.domain.dto;

import com.jungle.chalnaServer.domain.location.domain.entity.Encounter;

public class EncounterResponse {
    public record LOCATION(Long memberId, Long otherId,Double latitude, Double longitude) {}

    public static EncounterResponse.LOCATION of(Encounter encounter) {
        return new EncounterResponse.LOCATION(
                encounter.getMemberId(),
                encounter.getOtherId(),
                encounter.getLatitude(),
                encounter.getLongitude()
        );
    }

}

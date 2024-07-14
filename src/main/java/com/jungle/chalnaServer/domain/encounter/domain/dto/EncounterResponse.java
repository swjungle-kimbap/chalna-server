package com.jungle.chalnaServer.domain.encounter.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jungle.chalnaServer.domain.encounter.domain.entity.Encounter;

import java.time.LocalDateTime;

public class EncounterResponse {

    public record LOCATION(Double latitude, Double longitude,  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime meetTime) {}

    public static EncounterResponse.LOCATION of(Encounter encounter) {
        return new EncounterResponse.LOCATION(
                encounter.getLatitude(),
                encounter.getLongitude(),
                encounter.getCreatedAt()
        );
    }

}

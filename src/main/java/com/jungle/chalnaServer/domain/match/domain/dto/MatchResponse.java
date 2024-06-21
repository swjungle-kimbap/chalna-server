package com.jungle.chalnaServer.domain.match.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class MatchResponse {
    private Object data;


    public static MatchResponse of(String message) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("message", message);

        return MatchResponse.builder()
                .data(messageMap)
                .build();
    }

}

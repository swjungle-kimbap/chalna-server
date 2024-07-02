package com.jungle.chalnaServer.domain.match.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;



public class MatchRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Send {
        private List<String> deviceIdList;
        private String message;
    }
}

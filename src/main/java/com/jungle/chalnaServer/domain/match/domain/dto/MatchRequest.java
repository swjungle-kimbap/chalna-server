package com.jungle.chalnaServer.domain.match.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MatchRequest {
    public static class Send {
        private List<String> receiverList;
        private String message;
        private List<String> interestTag;
    }
}

package com.jungle.chalnaServer.domain.match.domain.dto;

import com.jungle.chalnaServer.global.common.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;



public class MatchRequest {

    @Getter
    @AllArgsConstructor
    public static class Send {
        private List<String> deviceIdList;
        private String content;
        private MessageType contentType;
    }
}

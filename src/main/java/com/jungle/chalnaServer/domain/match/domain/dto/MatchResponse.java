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
    public static Map<String, String> MatchMessageSend(String message) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("message", message);

        return messageMap;
    }

    public static Map<String, String> MatchReject(String message) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("message", message);

        return messageMap;
    }

    public static Map<String, String> MatchAccept(String chatRoomId) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("chatRoomId", chatRoomId);

        return messageMap;
    }
}

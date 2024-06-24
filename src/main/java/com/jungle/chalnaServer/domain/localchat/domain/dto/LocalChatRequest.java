package com.jungle.chalnaServer.domain.localchat.domain.dto;

public class LocalChatRequest {
    public record RADIUS(Double latitude, Double longitude, Double distance){

    }
    public record ADD(String name,Double latitude,Double longitude){

    }
}

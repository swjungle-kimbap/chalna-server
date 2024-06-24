package com.jungle.chalnaServer.domain.localchat.domain.dto;

import com.jungle.chalnaServer.domain.localchat.domain.entity.LocalChat;

public record LocalChatResponse(Long id, String name, Integer memberCount, Double latitude, Double longitude) {
    public static LocalChatResponse of(LocalChat localChat) {
        return new LocalChatResponse(localChat.getId()
                , localChat.getName()
                , localChat.getMemberCount()
                , localChat.getLatitude()
                , localChat.getLongitude());
    }
}

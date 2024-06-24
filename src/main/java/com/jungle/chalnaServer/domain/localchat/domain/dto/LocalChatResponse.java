package com.jungle.chalnaServer.domain.localchat.domain.dto;

import com.jungle.chalnaServer.domain.localchat.domain.entity.LocalChat;

public record LocalChatResponse(Long id, Long ownerId,String name, String description, Long chatRoomId, Double latitude,
                                Double longitude) {
    public static LocalChatResponse of(LocalChat localChat) {
        return new LocalChatResponse(localChat.getId()
                ,localChat.getOwnerId()
                , localChat.getName()
                , localChat.getDescription()
                , localChat.getChatRoom().getId()
                , localChat.getLatitude()
                , localChat.getLongitude());
    }
}

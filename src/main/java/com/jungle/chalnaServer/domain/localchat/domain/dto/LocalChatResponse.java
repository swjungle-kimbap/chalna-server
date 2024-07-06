package com.jungle.chalnaServer.domain.localchat.domain.dto;

import com.jungle.chalnaServer.domain.localchat.domain.entity.LocalChat;

public class LocalChatResponse {
    public record LOCAL_CHAT(Long id, Long ownerId, String name, String description, Long chatRoomId, Double latitude,
                             Double longitude) {
        public static LOCAL_CHAT of(LocalChat localChat) {
            return new LOCAL_CHAT(
                    localChat.getId(),
                    localChat.getOwnerId(),
                    localChat.getName(),
                    localChat.getDescription(),
                    localChat.getChatRoom().getId(),
                    localChat.getLatitude(),
                    localChat.getLongitude()
            );
        }
    }

    public record LOCAL_CHAT_INFO(LOCAL_CHAT localChat, boolean isOwner, boolean isJoined) {
        public static LOCAL_CHAT_INFO of(LocalChat localChat, boolean isOwner, boolean isJoined) {
            return new LOCAL_CHAT_INFO(
                    LOCAL_CHAT.of(localChat), isOwner, isJoined
            );
        }
    }

}

package com.jungle.chalnaServer.domain.friend.domain.dto;

public class FriendReponse {
    public record DETAIL(Long id,String username,String message,String profileImageUrl,Long chatRoomId){

    }
}
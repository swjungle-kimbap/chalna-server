package com.jungle.chalnaServer.domain.friend.domain.dto;

import com.jungle.chalnaServer.domain.friend.domain.entity.Request;

import java.time.LocalDateTime;

public class FriendReponse {
    public record DETAIL(Long id,String username,String message,Long profileImageUrl,Long chatRoomId){

    }

    public record REQUEST(Long id, Long memberId, Long otherId, Long chatRoomId, String username, LocalDateTime createdAt, LocalDateTime updatedAt) {
        public static REQUEST of(Request request) {
            return new REQUEST(
                    request.getId(),
                    request.getMemberId(),
                    request.getOtherId(),
                    request.getChatRoomId(),
                    request.getUsername(),
                    request.getCreatedAt(),
                    request.getUpdatedAt()
                    );
        }
    }

}

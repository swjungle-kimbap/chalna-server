package com.jungle.chalnaServer.domain.friend.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jungle.chalnaServer.domain.friend.domain.entity.Request;

import java.time.LocalDateTime;

public class FriendReponse {
    public record DETAIL(Long id,String username,String message,Long profileImageId,Long chatRoomId,String deviceId){

    }

    public record REQUEST(Long id, Long senderId, Long receiverId, Long chatRoomId, String username,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt) {
        public static REQUEST of(Request request) {
            return new REQUEST(
                    request.getId(),
                    request.getSenderId(),
                    request.getReceiverId(),
                    request.getChatRoomId(),
                    request.getUsername(),
                    request.getCreatedAt()
                    );
        }
    }

}

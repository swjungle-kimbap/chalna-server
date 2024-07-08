package com.jungle.chalnaServer.domain.chat.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoomMember;

import java.time.LocalDateTime;

public class ChatRoomMemberResponse {


    public record INFO(Long memberId,@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime lastLeaveAt){
        public static INFO of(ChatRoomMember member) {
            return new INFO(member.getMember().getId(), member.getLastLeaveAt());
        }
    }
}

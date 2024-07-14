package com.jungle.chalnaServer.domain.chat.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoomMember;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomMemberResponse {
    public record MEMBERS(List<INFO> members,int memberCount){

    }
    public record INFO(Long memberId,String username,Long profileImageId,boolean isJoined){
        public static INFO of(ChatRoomMember chatRoomMember) {
            return new INFO(
                    chatRoomMember.getMember().getId(),
                    chatRoomMember.getUserName(),
                    chatRoomMember.getMember().getProfileImageId(),
                    chatRoomMember.isJoined()
            );
        }
    }
    public record ENTER(Long memberId, @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime lastLeaveAt){
        public static ENTER of(ChatRoomMember member) {
            return new ENTER(member.getMember().getId(), member.getLastLeaveAt());
        }
    }
}

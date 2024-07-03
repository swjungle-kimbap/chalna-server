package com.jungle.chalnaServer.domain.chatRoom.domain.dto;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public  class MemberInfo {
    private Long memberId;
    private String username;


    public static MemberInfo of(Member member) {
        return new MemberInfo(member.getId(),member.getUsername());
    }

}

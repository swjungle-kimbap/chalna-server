package com.jungle.chalnaServer.domain.member.domain.dto;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;

public record MemberResponse(Long id, String username, String message, String profileImageUrl) {
    public static MemberResponse of(Member member){
        return new MemberResponse(member.getId(), member.getUsername(), member.getMessage(), member.getProfileImageUrl());
    }
}

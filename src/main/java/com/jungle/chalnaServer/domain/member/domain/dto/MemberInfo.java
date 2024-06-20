package com.jungle.chalnaServer.domain.member.domain.dto;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;

public record MemberInfo(Long id, String username, String message, String profileImageUrl) {
    public static MemberInfo of(Member member){
        return new MemberInfo(member.getId(), member.getUsername(), member.getMessage(), member.getProfileImageUrl());
    }
}

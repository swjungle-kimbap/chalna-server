package com.jungle.chalnaServer.domain.member.domain.dto;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private String username;
    private String message;
    private String profileImageUrl;

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .username(member.getUsername())
                .message(member.getMessage())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}



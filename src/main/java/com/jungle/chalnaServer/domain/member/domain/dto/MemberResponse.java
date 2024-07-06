package com.jungle.chalnaServer.domain.member.domain.dto;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;

public record MemberResponse(Long id, String username, String message) {
    public static MemberResponse of(Member member){
        return new MemberResponse(member.getId(), member.getUsername(), member.getMessage());
    }

    public record PROFILE_IMAGE_UPLOAD(Long fileId, String presignedUrl) {
        public static PROFILE_IMAGE_UPLOAD of(Long fileId, String presignedUrl) {
            return new PROFILE_IMAGE_UPLOAD(fileId,presignedUrl);
        }
    }

}

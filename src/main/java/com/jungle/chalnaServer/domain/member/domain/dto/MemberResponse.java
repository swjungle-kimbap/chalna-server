package com.jungle.chalnaServer.domain.member.domain.dto;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.infra.file.domain.dto.FileResponse;

public class MemberResponse {
    public record INFO(Long id, String username, String message, Long profileImageId) {
        public static INFO of(Member member) {
            return new INFO(member.getId(), member.getUsername(), member.getMessage(), member.getProfileImageId());
        }
    }

    public record PROFILE_IMAGE_UPLOAD(Long fileId, String presignedUrl) {
        public static PROFILE_IMAGE_UPLOAD of(FileResponse.UPLOAD upload) {
            return new PROFILE_IMAGE_UPLOAD(upload.fileId(),upload.presignedUrl());
        }
    }

}
package com.jungle.chalnaServer.domain.auth.domain.entity;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;

public record AuthInfo(Long id, String deviceId, String fcmToken, String refreshToken) {
    public static AuthInfo of(Member member) {
        return new AuthInfo(member.getId(), member.getDeviceId(), member.getFcmToken(), member.getRefreshToken());
    }
}

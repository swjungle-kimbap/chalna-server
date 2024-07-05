package com.jungle.chalnaServer.domain.auth.domain.entity;

public record AuthInfo(Long id, String deviceId,String fcmToken, String refreshToken) {

    public AuthInfo updateRefreshToken(String refreshToken) {
        return new AuthInfo(this.id, this.deviceId,this.fcmToken, refreshToken);
    }
}

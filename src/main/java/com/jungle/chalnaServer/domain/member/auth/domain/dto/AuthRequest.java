package com.jungle.chalnaServer.domain.member.auth.domain.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthRequest {

    private  Integer kakaoId;
    private  String username;
    private  String loginToken;
    private  String devicedId;
    private  String fcmToken;
}
package com.jungle.chalnaServer.domain.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    private  Long kakaoId;
    private  String username;
    private  String loginToken;
    private  String deviceId;
    private  String fcmToken;
    private String accessToken; //kakao
    private String refreshToken; //kakao

    public record SIGNUP(Long kakaoId, String username, String accessToken, String refreshToken){

    }

    public record TEMPSIGNUP(Long kakaoId, String username) {}


    public record LOGIN(String loginToken,String deviceId,String fcmToken){

    }
}

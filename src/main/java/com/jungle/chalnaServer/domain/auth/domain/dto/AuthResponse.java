package com.jungle.chalnaServer.domain.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String loginToken;
    private  Long kakaoId;
    private  String username;

    public static AuthResponse of(String loginToken, Long kakaoId, String username) {
        return AuthResponse.builder()
                .loginToken(loginToken)
                .kakaoId(kakaoId)
                .username(username)
                .build();
    }

//    public record KakaoUserInfo(Integer kakaoId, String username) {}

    @Getter
    @AllArgsConstructor
    public static class KakaoUserInfo {
        @JsonProperty("id")
        private Integer kakaoId;
        @JsonProperty("properties")
        private Properties properties;

        @Getter
        @AllArgsConstructor
        public static class Properties {
            private String nickname;
        }
    }


}

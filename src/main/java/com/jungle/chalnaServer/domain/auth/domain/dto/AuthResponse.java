package com.jungle.chalnaServer.domain.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String loginToken;

    public static AuthResponse of(String loginToken) {
        return AuthResponse.builder()
                .loginToken(loginToken)
                .build();
    }
}

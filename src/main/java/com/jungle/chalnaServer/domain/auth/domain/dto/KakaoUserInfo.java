package com.jungle.chalnaServer.domain.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfo {

//    @JsonProperty("kakao_account")
//    private Map<String, Object> kakaoAccount;
//    @JsonProperty("properties")
//    private Map<String, Object> properties;

    @JsonProperty("id")
    private Long kakaoId;
    @JsonProperty("properties")
    private Properties properties;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Properties {
        @JsonProperty("nickname")
        private String nickname;
    }


}

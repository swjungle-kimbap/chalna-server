package com.jungle.chalnaServer.domain.auth.service;

import com.jungle.chalnaServer.domain.auth.domain.dto.KakaoUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class KakaoTokenService {

    private final RestTemplate restTemplate;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    public KakaoTokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /* 토큰의 유효성을 검증하는 로직 */
    public Boolean verifyToken(String kakaoToken) {
        String url = "https://kapi.kakao.com/v1/user/access_token_info";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                System.err.println("401 Unauthorized: " + e.getResponseBodyAsString());
            }
            return false;
        } catch (Exception e) {
            System.err.println("토큰 검증 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    /* 토큰으로 사용자 정보를 가져오는 로직 */
    public KakaoUserInfo getUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(url, HttpMethod.GET, entity, KakaoUserInfo.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("사용자 정보 가져오기 중 오류 발생: " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.err.println("사용자 정보 가져오기 중 오류 발생: " + e.getMessage());
            return null;
        }
    }

    /* 토큰을 갱신해오는 로직 */
    public String refreshToken(String refreshToken) {
        String url = "https://kauth.kakao.com/oauth/token?grant_type=refresh_token" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&refresh_token=" + refreshToken;

        try {
            ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(url, null, KakaoTokenResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody().getAccessToken();
            }
        } catch (HttpClientErrorException e) {
            System.err.println("토큰 갱신 중 오류 발생: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("토큰 갱신 중 오류 발생: " + e.getMessage());
        }
        return null;
    }

    public static class KakaoTokenResponse {
        private String access_token;

        public String getAccessToken() {
            return access_token;
        }

        public void setAccessToken(String access_token) {
            this.access_token = access_token;
        }
    }
}

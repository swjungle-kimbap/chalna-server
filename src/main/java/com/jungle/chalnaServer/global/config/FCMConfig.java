package com.jungle.chalnaServer.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FCMConfig {

    private static final Logger logger = LoggerFactory.getLogger(FCMConfig.class);

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // 환경 변수에서 서비스 계정 키 경로 가져오기
        String googleCredentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

        // 경로가 null 또는 비어있는지 확인
        if (googleCredentialsPath == null || googleCredentialsPath.trim().isEmpty()) {
            logger.error("Google application credentials path is not provided");
            throw new IOException("Google application credentials path is not provided");
        }

        logger.info("Initializing Firebase with provided service account key from environment variable");

        // 파일 입력 스트림을 통해 서비스 계정 키 로드
        try (InputStream serviceAccount = new FileInputStream(googleCredentialsPath)) {
            // Firebase 옵션 설정
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Firebase 초기화
            return FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}

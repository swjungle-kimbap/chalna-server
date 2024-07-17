package com.jungle.chalnaServer.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class   FCMConfig {

    private static final Logger logger = LoggerFactory.getLogger(FCMConfig.class);

    @Value("${firebase.service-account-key}")
    private String serviceAccountKey;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        if (serviceAccountKey == null || serviceAccountKey.trim().isEmpty()) {
            logger.error("Google application credentials are not provided");
            throw new IOException("Google application credentials are not provided");
        }

        logger.info("Initializing Firebase with provided service account key from application.yml");

        try (InputStream serviceAccount = new ByteArrayInputStream(serviceAccountKey.getBytes(StandardCharsets.UTF_8))) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            return FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}

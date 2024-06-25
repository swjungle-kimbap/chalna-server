package com.jungle.chalnaServer.infra.fcm;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.jungle.chalnaServer.infra.fcm.dto.FCMData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class FCMService {
    public static void sendFCM(String fcmToken, FCMData fcmData){
        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .putAllData(fcmData.toMap())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build());

            Message message = messageBuilder.build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            System.out.println("Failed to send FCM message" + e);
        }
    }
}
package com.jungle.chalnaServer.infra.fcm;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.jungle.chalnaServer.infra.fcm.dto.FCMData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {
    @Async("taskExecutor")
    public void sendFCMData(String fcmToken, FCMData fcmData){

        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .putAllData(fcmData.toMap())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build());
            Message message = messageBuilder.build();

            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
        }
    }
    public void sendFCMNotification(String fcmToken, String title, String body){
        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build());
            Message message = messageBuilder.build();

            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
        }
    }
}
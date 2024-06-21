package com.jungle.chalnaServer.infra.fcm.dto;

import lombok.Getter;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

@Getter
public class FCMData {
    private final String senderId;
    private final String message;
    private final String createdAt;
    private final Map<String, String> additionalData; //message 별로 추가 필요한 데이터 넣어주기

    private FCMData(String senderId, String message, String createdAt) {
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
        this.additionalData = new HashMap<>();
        this.additionalData.put("fcmType", "match");
    }

    private FCMData(String senderId, String message, String createdAt, String chatRoomId, String messageType) {
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
        this.additionalData = new HashMap<>();
        this.additionalData.put("fcmType", "chat");
        this.additionalData.put("chatRoomId", chatRoomId);
        this.additionalData.put("messageType", messageType);
    }


    /*인연 FCMData 생성자*/
    public static FCMData instanceOfMatchFCM(String senderId, String message, String createdAt) {
        return new FCMData(senderId, message, createdAt);
    }

    /*ChatFCMData 생성자*/
    public static FCMData instanceOfChatFCM(String senderId, String message, String createdAt, String chatRoomId, String messageType) {
        return new FCMData(senderId, message, createdAt, chatRoomId, messageType);
    }


    public Map<String, String> toMap() {
        Gson gson = new Gson();

        Map<String, String> map = new HashMap<>();
        map.put("senderId", this.senderId);
        map.put("message", this.message);
        map.put("createdAt", this.createdAt);
        map.put("additionalData", gson.toJson(this.additionalData));
        return map;
    }
}
package com.jungle.chalnaServer.infra.fcm.dto;

import lombok.Getter;
import com.google.gson.Gson;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FCMData {
    private final String senderId;
    private final String message;
    private final String createdAt;
    private final Map<String, String> additionalData; //message 별로 추가 필요한 데이터 넣어주기

    private FCMData(String senderId, String message, String createdAt, String notificationId) {
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
        this.additionalData = new HashMap<>();
        this.additionalData.put("fcmType", "match");
        this.additionalData.put("notificationId", notificationId);
    }

    private FCMData(String senderId, String message, String createdAt, String senderName, String chatRoomId, String chatRoomType) {
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
        this.additionalData = new HashMap<>();
        this.additionalData.put("fcmType", "chat");
        this.additionalData.put("senderName", senderName);
        this.additionalData.put("chatRoomId", chatRoomId);
        this.additionalData.put("chatRoomType", chatRoomType);
    }


    /*인연 FCMData 생성자*/
    public static FCMData instanceOfMatchFCM(String senderId, String message, String createdAt, String notificationId) {
        return new FCMData(senderId, message, createdAt, notificationId);
    }

    /*ChatFCMData 생성자*/
    public static FCMData instanceOfChatFCM(String senderId, String message, String createdAt, String senderName, String chatRoomId, String chatRoomType) {
        return new FCMData(senderId, message, createdAt, senderName, chatRoomId, chatRoomType);
    }


    public Map<String, String> toMap() {
        Gson gson = new Gson();

        Map<String, String> map = new HashMap<>();
        map.put("senderId", this.senderId);
        map.put("message", this.message);
        map.put("createdAt", this.createdAt);
        JSONObject additionalDataJson = new JSONObject(this.additionalData);
        map.put("additionalData", additionalDataJson.toString());
        return map;
    }
}

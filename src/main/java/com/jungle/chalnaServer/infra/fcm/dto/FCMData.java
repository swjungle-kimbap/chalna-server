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
    private final String fcmType;
    private final Map<String, String> additionalData; //message 별로 추가 필요한 데이터 넣어주기

    private FCMData(String senderId, String message, String notificationId, String receiverId) {
        this.senderId = senderId;
        this.message = message;
        this.fcmType = "match";
        this.additionalData = new HashMap<>();
        this.additionalData.put("notificationId", notificationId);
        this.additionalData.put("receiverId", receiverId);
    }

    private FCMData(String senderId, String message, String senderName, String chatRoomId, String chatRoomType, String messageType) {
        this.senderId = senderId;
        this.message = message;
        this.fcmType = "chat";
        this.additionalData = new HashMap<>();
        this.additionalData.put("senderName", senderName);
        this.additionalData.put("chatRoomId", chatRoomId);
        this.additionalData.put("chatRoomType", chatRoomType);
        this.additionalData.put("messageType", messageType);
    }


    /*인연 FCMData 생성자*/
    public static FCMData instanceOfMatchFCM(String senderId, String message, String notificationId, String receiverId) {
        return new FCMData(senderId, message, notificationId, receiverId);
    }

    /*ChatFCMData 생성자*/
    public static FCMData instanceOfChatFCM(String senderId, String message, String senderName, String chatRoomId, String chatRoomType, String messageType) {
        return new FCMData(senderId, message, senderName, chatRoomId, chatRoomType, messageType);
    }


    public Map<String, String> toMap() {
        Gson gson = new Gson();

        Map<String, String> map = new HashMap<>();
        map.put("senderId", this.senderId);
        map.put("message", this.message);
        map.put("fcmType", this.fcmType);
        JSONObject additionalDataJson = new JSONObject(this.additionalData);
        map.put("additionalData", additionalDataJson.toString());
        return map;
    }
}

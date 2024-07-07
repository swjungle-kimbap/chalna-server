package com.jungle.chalnaServer.infra.fcm.dto;

import com.google.gson.Gson;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FCMData {
    private final String senderId;
    private final String message;
    private final String fcmType;
    private final String additionalData; //message 별로 추가 필요한 데이터 넣어주기

    public static final String TYPE_MATCH = "match";
    public static final String TYPE_CHAT = "chat";

    private FCMData(String senderId, CONTENT message, String fcmType, Object data) {
        Gson gson = new Gson();
        this.senderId = senderId;
        this.message = gson.toJson(message);
        this.fcmType = fcmType;
        this.additionalData = gson.toJson(data);
    }


    /*인연 FCMData 생성자*/
    public static FCMData instanceOfMatchFCM(String senderId, CONTENT message, MATCH data) {
        return new FCMData(senderId, message, TYPE_MATCH, data);
    }

    /*ChatFCMData 생성자*/
    public static FCMData instanceOfChatFCM(String senderId, CONTENT message, CHAT data) {
        return new FCMData(senderId, message, TYPE_CHAT, data);
    }


    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("senderId", this.senderId);
        map.put("message", this.message);
        map.put("fcmType", this.fcmType);
        map.put("additionalData", additionalData);
        return map;
    }

    public record CHAT(String senderName, Long chatRoomId, ChatRoom.ChatRoomType chatRoomType,
                       ChatMessage.MessageType messageType) {
    }

    public record MATCH(Long notificationId, int overlapCount, Long receiverId) {

    }

    public record CONTENT(String content, String contentType){
        public static String TYPE_FILE = "FILE";
        public static String TYPE_MESSAGE = "MESSAGE";

        public static CONTENT message(String message){
            return new CONTENT(message, TYPE_MESSAGE);
        }
        public static CONTENT file(String url){
            return new CONTENT(url, TYPE_FILE);
        }
    }

}

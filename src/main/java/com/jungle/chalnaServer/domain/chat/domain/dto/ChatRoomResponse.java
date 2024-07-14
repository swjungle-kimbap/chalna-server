package com.jungle.chalnaServer.domain.chat.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomResponse {
    @Getter
    public static class CHATROOM {
        private final Long id;
        private final ChatRoom.ChatRoomType type;
        private final ChatRoomMemberResponse.MEMBERS chatRoomMemberInfo;
        private final ChatMessageResponse.MESSAGE recentMessage;
        private final Integer unreadMessageCount;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private final LocalDateTime lastReceivedAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private final LocalDateTime createdAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private final LocalDateTime updatedAt;


        public CHATROOM(ChatRoom chatRoom, ChatRoomMemberResponse.MEMBERS chatRoomMemberInfo, ChatMessageResponse.MESSAGE recentMessage, Integer unreadMessageCount, LocalDateTime lastReceivedAt) {
            this.id = chatRoom.getId();
            this.type = chatRoom.getType();
            this.chatRoomMemberInfo = chatRoomMemberInfo;
            this.recentMessage = recentMessage;
            this.lastReceivedAt = lastReceivedAt;
            this.unreadMessageCount = unreadMessageCount;
            this.createdAt = chatRoom.getCreatedAt();
            this.updatedAt = chatRoom.getUpdatedAt();
        }
    }
    @Getter
    public static class MESSAGES{
        private final Long id;
        private final ChatRoom.ChatRoomType type;
        private final ChatRoomMemberResponse.MEMBERS chatRoomMemberInfo;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private final LocalDateTime createdAt;

        private final List<ChatMessageResponse.MESSAGE> messages;

        public MESSAGES(ChatRoom chatRoom, ChatRoomMemberResponse.MEMBERS chatRoomMemberInfo, List<ChatMessageResponse.MESSAGE> messages) {
            this.id = chatRoom.getId();
            this.type = chatRoom.getType();
            this.chatRoomMemberInfo = chatRoomMemberInfo;
            this.createdAt = chatRoom.getCreatedAt();
            this.messages = messages;

        }
    }


}

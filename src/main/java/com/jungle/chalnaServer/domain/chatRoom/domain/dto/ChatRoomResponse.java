package com.jungle.chalnaServer.domain.chatRoom.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomResponse {
    @Getter
    public static class CHATROOM {
        private final Long id;
        private final ChatRoom.ChatRoomType type;
        private final Integer memberCount;
        private final List<MemberInfo> members;
        private final ChatMessageResponse.MESSAGE recentMessage;
        private final Integer unreadMessageCount;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedAt;


        public CHATROOM(ChatRoom chatRoom, List<MemberInfo> memberInfos, ChatMessageResponse.MESSAGE recentMessage, Integer unreadMessageCount) {
            this.id = chatRoom.getId();
            this.type = chatRoom.getType();
            this.memberCount = chatRoom.getMemberCount();
            this.members = memberInfos;
            this.recentMessage = recentMessage;
            this.unreadMessageCount = unreadMessageCount;
            this.createdAt = chatRoom.getCreatedAt();
            this.updatedAt = chatRoom.getUpdatedAt();
        }
    }
    @Getter
    public static class MESSAGES{
        private final Long id;
        private final ChatRoom.ChatRoomType type;
        private final List<MemberInfo> members;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private final LocalDateTime createdAt;

        private final List<ChatMessageResponse.MESSAGE> messages;

        public MESSAGES(ChatRoom chatRoom,List<MemberInfo> memberInfos, List<ChatMessageResponse.MESSAGE> messages) {
            this.id = chatRoom.getId();
            this.type = chatRoom.getType();
            this.members = memberInfos;
            this.createdAt = chatRoom.getCreatedAt();
            this.messages = messages;

        }
    }


}

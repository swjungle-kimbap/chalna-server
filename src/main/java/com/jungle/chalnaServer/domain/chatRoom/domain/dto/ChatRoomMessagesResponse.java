package com.jungle.chalnaServer.domain.chatRoom.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ChatRoomMessagesResponse {
    private Long id;
    private ChatRoom.ChatRoomType type;
    private List<MemberInfo> members;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private List<ChatMessageResponse> list;

    public ChatRoomMessagesResponse(ChatRoom chatRoom, List<ChatMessageResponse> chatRoomResponses) {
        this.id = chatRoom.getId();
        this.type = chatRoom.getType();
        this.members = chatRoom.getMembers().stream()
                .map(chatRoomMember -> {
                    return new MemberInfo(
                            chatRoomMember.getMember().getId(),
                            chatRoomMember.getMember() != null ? chatRoomMember.getMember().getUsername() : null
                    );
                })
                .collect(Collectors.toList());
        this.createdAt = chatRoom.getCreatedAt();
        this.list = chatRoomResponses;

    }


}

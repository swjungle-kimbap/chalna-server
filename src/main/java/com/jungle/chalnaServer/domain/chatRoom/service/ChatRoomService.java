package com.jungle.chalnaServer.domain.chatRoom.service;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import com.jungle.chalnaServer.domain.chatRoom.domain.dto.ChatRoomResponse;
import com.jungle.chalnaServer.domain.chatRoom.domain.dto.MemberInfo;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomMemberRepository;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomService.class);
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    private final MemberRepository memberRepository;

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 채팅방 목록 요청
    public List<ChatRoomResponse> getChatRoomList(Long memberId) {
        List<ChatRoomMember> chatroomMembers = chatRoomMemberRepository.findByMemberId(memberId);
        List<ChatRoomResponse> list = new ArrayList<>();
        for (ChatRoomMember chatroomMember : chatroomMembers) {
            ChatRoom chatRoom = chatroomMember.getChatRoom();
            ChatMessage recentMessage = chatRepository.getLatestMessage(chatRoom.getId());
            List<MemberInfo> memberInfos = chatRoom.getMembers().stream()
                    .map(member -> {
                        Member memberEntity = memberRepository.findById(member.getMemberId()).orElse(null);
                        return new MemberInfo(
                                member.getMemberId(),
                                memberEntity != null ? memberEntity.getUsername() : null
                        );
                    })
                    .collect(Collectors.toList());
            ChatRoomResponse apply = new ChatRoomResponse(chatRoom, memberInfos, new ChatMessageResponse(recentMessage));
            list.add(apply);
        } return list;
    }

    // 채팅방 메시지 요청
    public List<ChatMessageResponse> getChatMessages(Long memberId, Long chatRoomId, LocalDateTime lastLeaveAt) {
        return chatRepository.getMessagesAfterUpdateDate(memberId, chatRoomId, lastLeaveAt).stream()
                .map(message -> {
                    ChatMessageResponse chatMessageResponse = new ChatMessageResponse(message);
                    return chatMessageResponse;
                })
                .collect(Collectors.toList());
    }

    // 채팅방 만들기
    @Transactional
    public Long makeChatRoom(ChatRoom.ChatRoomType type, Integer memberCount, List<Long> memberIdList) {
        ChatRoom chatRoom = new ChatRoom(type, memberCount);
        chatRoomRepository.save(chatRoom);

        for (Long memberId : memberIdList) {
            ChatRoomMember chatRoomMember = new ChatRoomMember(memberId, chatRoom);
            chatRoomMemberRepository.save(chatRoomMember);
        }

        return chatRoom.getId();
    }
}

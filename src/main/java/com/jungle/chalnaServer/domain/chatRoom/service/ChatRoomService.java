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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomService.class);
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    private final MemberRepository memberRepository;

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 채팅방 목록 요청
    public List<ChatRoomResponse> getChatRoomList(Long memberId) {
        List<ChatRoomMember> chatroomMembers = chatRoomMemberRepository.findByMemberId(memberId);
        List<ChatRoomResponse> list = new ArrayList<>();
        for (ChatRoomMember chatroomMember : chatroomMembers) {
            if (!chatroomMember.getIsRemoved()) {
                ChatRoom chatRoom = chatroomMember.getChatRoom();
                ChatMessage recentMessage = chatRepository.getLatestMessage(chatRoom.getId());
                List<MemberInfo> memberInfos = chatRoom.getMembers().stream()
                        .map(chatRoomMember -> {
//                        Member memberEntity = memberRepository.findById(member.getMember().getId()).orElse(null);
                            return new MemberInfo(
                                    chatRoomMember.getMember().getId(),
                                    chatRoomMember.getMember() != null ? chatRoomMember.getMember().getUsername() : null
                            );
                        })
                        .collect(Collectors.toList());
                ChatRoomResponse apply = new ChatRoomResponse(chatRoom, memberInfos, new ChatMessageResponse(recentMessage));
                list.add(apply);
            }
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
            Member member = memberRepository.findById(memberId).orElse(null);
            ChatRoomMember chatRoomMember = new ChatRoomMember(member, chatRoom);
            chatRoomMemberRepository.save(chatRoomMember);
        }
        scheduleRoomTermination(chatRoom.getId(), 5, TimeUnit.MINUTES);

        return chatRoom.getId();
    }

    // 채팅방 5분 스케줄러
    public void scheduleRoomTermination(Long chatRoomId, long delay, TimeUnit unit) {
        scheduler.schedule(() -> {
            log.info("timeout roomId {}", chatRoomId);
            Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
            optionalChatRoom.ifPresent(chatRoom -> {
                // 채팅방의 상태를 대기 상태로 변경
                chatRoom.updateType(ChatRoom.ChatRoomType.WAITING);
                chatRoomRepository.save(chatRoom);

                // 메시지 보내기
                Long messageId = chatRepository.makeMessageId();
                LocalDateTime now = LocalDateTime.now();
                String content = "5분이 지났습니다.";

                ChatMessageResponse chatMessage = ChatMessageResponse.builder()
                        .id(messageId)
                        .content(content)
                        .senderId(0L)
                        .type(ChatMessage.MessageType.TIMEOUT)
                        .status(true)
                        .createdAt(now)
                        .build();


                messagingTemplate.convertAndSend("/topic/" + chatRoomId, chatMessage);

                ChatMessage message = new ChatMessage(messageId, ChatMessage.MessageType.TIMEOUT, 0L,
                        chatRoomId, content, true,
                        now, now);

                chatRepository.saveMessage(message);

            });
        }, delay, unit);
    }

    // 채팅방 나가기(삭제)
    public Boolean leaveChatRoom(Long memberId, Long chatRoomId) {
        Optional<ChatRoomMember> chatRoomMemberOptional = chatRoomMemberRepository.findByMemberIdAndChatRoomId(memberId, chatRoomId);

        if (chatRoomMemberOptional.isPresent()) {
            ChatRoomMember chatRoomMember = chatRoomMemberOptional.get();
            chatRoomMember.removeMember();
            chatRoomMemberRepository.save(chatRoomMember);

            // 채팅방 인원 변경
            chatRoomMember.getChatRoom().updateMemberCount(chatRoomMember.getChatRoom().getMemberCount() - 1);
            chatRoomRepository.save(chatRoomMember.getChatRoom());
            return true;
        } else {
            // 에러 처리
            log.info("예외 처리");
            return false;
        }
    }
}

package com.jungle.chalnaServer.domain.chatRoom.service;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageRequest;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.handler.StompHandler;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import com.jungle.chalnaServer.domain.chat.service.ChatService;
import com.jungle.chalnaServer.domain.chatRoom.domain.dto.ChatRoomResponse;
import com.jungle.chalnaServer.domain.chatRoom.domain.dto.MemberInfo;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chatRoom.exception.ChatRoomMemberNotFoundException;
import com.jungle.chalnaServer.domain.chatRoom.exception.ChatRoomNotFoundException;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomMemberRepository;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.global.util.RandomUserNameService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RandomUserNameService randomUserNameService;

    private final SimpMessagingTemplate messagingTemplate;
    private final StompHandler stomphandler;
    private final ChatService chatService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    // 채팅방 목록 요청
    public List<ChatRoomResponse.CHATROOM> getChatRoomList(Long memberId) {
        List<ChatRoomMember> chatroomMembers = chatRoomMemberRepository.findByMemberId(memberId);
        return chatroomMembers.stream()
                .map(chatRoomMember -> {
                    ChatRoom chatRoom = chatRoomMember.getChatRoom();
                    ChatMessage recentMessage = chatRepository.getLatestMessage(chatRoom.getId());

                    Integer unreadMessageCount = chatRepository.getUnreadCount(chatRoom.getId(), chatRoomMember.getLastLeaveAt());
                    List<MemberInfo> memberInfos = getChatRoomMembers(chatRoom);
                    ChatMessageResponse.MESSAGE recentMessageRes = recentMessage != null ? ChatMessageResponse.MESSAGE.of(recentMessage) : null;
                    return new ChatRoomResponse.CHATROOM(chatRoom, memberInfos, recentMessageRes, unreadMessageCount);
                })
                .sorted(Comparator.comparing((c) ->
                                c.getRecentMessage() == null ? null : c.getRecentMessage().createdAt()
                        , Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    // 채팅방 메시지 요청
    @Transactional
    public ChatRoomResponse.MESSAGES getChatMessages(Long memberId, Long chatRoomId) {
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberIdAndChatRoomId(memberId, chatRoomId).orElseThrow(ChatRoomMemberNotFoundException::new);
        LocalDateTime lastLeaveAt = chatRoomMember.getLastLeaveAt();
        chatRoomMember.updateLastLeaveAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        List<ChatMessageResponse.MESSAGE> messages = chatRepository.getMessagesAfterUpdateDate(memberId, chatRoomId, lastLeaveAt).stream()
                .map(ChatMessageResponse.MESSAGE::of)
                .toList();

        return new ChatRoomResponse.MESSAGES(chatRoomMember.getChatRoom(), getChatRoomMembers(chatRoomMember.getChatRoom()), messages);
    }

    // 채팅방 만들기
    @Transactional
    public Long makeChatRoom(ChatRoom.ChatRoomType type, List<Long> memberIdList) {
        log.info("chatroom created");
        ChatRoom chatRoom = new ChatRoom(type, Set.copyOf(memberIdList));
        chatRoomRepository.save(chatRoom);

        log.info("chatroom member join start");
        for (Long memberId : memberIdList) {
            Member member = memberRepository.findById(memberId).orElse(null);
            if (member == null) {
                continue;
            }
            log.info("chatroom member {} joined", member.getId());
            ChatRoomMember chatRoomMember = new ChatRoomMember(member, chatRoom);
            if (type != ChatRoom.ChatRoomType.FRIEND)
                chatRoomMember.updateDisplayName(randomUserNameService.getRandomUserName());
            chatRoomMemberRepository.save(chatRoomMember);

            stomphandler.setMemberOffline(chatRoom.getId(), member.getId());
        }
        if (type == ChatRoom.ChatRoomType.MATCH)
            scheduleRoomTermination(chatRoom.getId(), 5, TimeUnit.MINUTES);

        return chatRoom.getId();
    }

    // 채팅방 5분 스케줄러
    public void scheduleRoomTermination(Long chatRoomId, long delay, TimeUnit unit) {
        scheduler.schedule(() -> {
            log.info("timeout roomId {}", chatRoomId);
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
            if (chatRoom == null) {
                return;
            }
            if (chatRoom.getType().equals(ChatRoom.ChatRoomType.MATCH)) {
                // 채팅방의 상태를 대기 상태로 변경
                chatRoom.updateType(ChatRoom.ChatRoomType.WAITING);
                chatRoomRepository.save(chatRoom);
                // 메시지 보내기
                Long messageId = chatRepository.getMessageId();
                LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
                ChatMessageResponse.MESSAGE res = new ChatMessageResponse.MESSAGE(
                        messageId
                        , ChatMessage.MessageType.TIMEOUT
                        , "5분이 지났습니다.\n대화를 이어가려면 친구요청을 보내보세요."
                        , 0L
                        , 0
                        , now
                );
                ChatMessageRequest.SEND req = new ChatMessageRequest.SEND(ChatMessage.MessageType.TIMEOUT, "5분이 지났습니다.\n대화를 이어가려면 친구요청을 보내보세요.");
                chatService.sendMessage(0L,chatRoomId,req);
            }
        }, delay, unit);
    }

    // 채팅방 나가기(삭제)
    @Transactional
    public void leaveChatRoom(Long chatRoomId, Long memberId) {
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberIdAndChatRoomId(memberId, chatRoomId).orElseThrow(ChatRoomMemberNotFoundException::new);
        // 채팅방 인원 변경
        chatRoomMember.getChatRoom().getMemberIdList().remove(memberId);
        // session에서 삭제
        stomphandler.setMemberOnline(chatRoomId, memberId);
        // entity 삭제
        chatRoomMemberRepository.delete(chatRoomMember);
    }

    @Transactional
    public void joinChatRoom(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        // 채팅방에 있는지 확인
        for (ChatRoomMember chatRoomMember : chatRoom.getMembers()) {
            // 채팅방에 이미 있으면 pass
            if (chatRoomMember.getMember().getId().equals(member.getId()))
                return;
        }
        chatRoom.getMemberIdList().add(memberId);
        ChatRoomMember chatRoomMember = new ChatRoomMember(member, chatRoom);
        if(chatRoom.getType() != ChatRoom.ChatRoomType.FRIEND)
            chatRoomMember.updateDisplayName(randomUserNameService.getRandomUserName());
        chatRoomMemberRepository.save(chatRoomMember);
        stomphandler.setMemberOffline(chatRoom.getId(), member.getId());
    }

    private List<MemberInfo> getChatRoomMembers(ChatRoom chatRoom) {
        List<MemberInfo> list = new ArrayList<>();
        for (ChatRoomMember member : chatRoom.getMembers()) {
            if (chatRoom.getType() == ChatRoom.ChatRoomType.FRIEND) {
                list.add(MemberInfo.of(member.getMember()));
            } else {
                list.add(new MemberInfo(member.getMember().getId(), member.getDisplayName()));
            }
        }
        return list;
    }
}

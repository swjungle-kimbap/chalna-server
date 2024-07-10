package com.jungle.chalnaServer.domain.chat.service;

import com.jungle.chalnaServer.domain.auth.domain.entity.AuthInfo;
import com.jungle.chalnaServer.domain.auth.repository.AuthInfoRepository;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageRequest;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatRoomResponse;
import com.jungle.chalnaServer.domain.chat.domain.dto.MemberInfo;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chat.exception.ChatRoomMemberNotFoundException;
import com.jungle.chalnaServer.domain.chat.exception.ChatRoomNotFoundException;
import com.jungle.chalnaServer.domain.chat.handler.StompHandler;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import com.jungle.chalnaServer.domain.chat.repository.ChatRoomMemberRepository;
import com.jungle.chalnaServer.domain.chat.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.localchat.domain.entity.LocalChat;
import com.jungle.chalnaServer.domain.localchat.repository.LocalChatRepository;
import com.jungle.chalnaServer.domain.localchat.service.LocalChatService;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.domain.relation.domain.entity.FriendStatus;
import com.jungle.chalnaServer.domain.relation.domain.entity.Relation;
import com.jungle.chalnaServer.domain.relation.repository.RelationRepository;
import com.jungle.chalnaServer.domain.relation.service.RelationService;
import com.jungle.chalnaServer.global.util.GeoHashService;
import com.jungle.chalnaServer.global.util.RandomUserNameService;
import com.jungle.chalnaServer.infra.fcm.FCMService;
import com.jungle.chalnaServer.infra.fcm.dto.FCMData;
import com.jungle.chalnaServer.infra.file.domain.dto.FileResponse;
import com.jungle.chalnaServer.infra.file.service.FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
@Log4j2
@RequiredArgsConstructor
public class ChatService {

    private final StompHandler stomphandler;
    private final SimpMessagingTemplate messagingTemplate;

    private final FileService fileService;
    private final FCMService fcmService;
    private final RelationService relationService;
    private final RandomUserNameService randomUserNameService;
    private final GeoHashService geoHashService;

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AuthInfoRepository authInfoRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final RelationRepository relationRepository;
    private final LocalChatRepository localChatRepository;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Transactional
    // 채팅 보내기(+push 알림)
    public void sendMessage(Long memberId, Long roomId, ChatMessageRequest.SEND req) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);

        // 친구 채팅일때 없으면 member에 추가하기
        Relation relation = relationRepository.findByIdAndChatRoom(memberId, chatRoom).orElse(null);
        if (relation != null) {
            Relation reverse = relationService.findRelation(relation.getRelationPK().reverse());
            if (!reverse.isBlocked()
                    && relation.getFriendStatus() == FriendStatus.ACCEPTED) {
                joinChatRoom(roomId, reverse.getRelationPK().getId());
            }
        }

        // 메시지 생성 및 저장
        FCMData.CONTENT content;
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        if (req.type().equals(ChatMessage.MessageType.FILE)) {
            content = FCMData.CONTENT.file(sendFile(memberId, roomId, Long.valueOf(req.content()), now));
        } else {
            saveMessage(memberId, roomId, req.content(), req.type(),now);
            content = FCMData.CONTENT.message(req.content());
        }

        // 보낸사람 이름 찾기
        String senderName;
        ChatRoomMember sender = chatRoomMemberRepository.findByMemberIdAndChatRoomId(memberId, roomId).orElse(null);
        if(sender != null)
            senderName = sender.getUserName();
        else
            senderName = "인연 채팅 알림";

        // push 알림 보내기. 채팅룸에 멤버 정보를 확인해서 다른 멤버가 채팅방에 없는 경우 알림 보내기
        sendChatFCMAlarm(chatRoom,memberId,senderName,content,req.type());
    }

    private void sendChatFCMAlarm(ChatRoom chatRoom, Long senderId, String senderName, FCMData.CONTENT content, ChatMessage.MessageType type){
        log.info("fcm send start by [{},{}]", senderId,senderName);
        if (stomphandler.getOfflineMemberCount(chatRoom.getId()) > 0) {
            Set<Long> offlineMembers = stomphandler.getOfflineMembers(chatRoom.getId()); // 오프라인 유저 정보
            List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoomId(chatRoom.getId());
            for (ChatRoomMember chatRoomMember : members) {
                Long receiverId = chatRoomMember.getMember().getId();
                log.info("fcm send start to {}",receiverId);
                if(offlineMembers.contains(receiverId) && !receiverId.equals(senderId)) {
                    AuthInfo authInfo = authInfoRepository.findById(receiverId);
                    FCMData fcmData = FCMData.instanceOfChatFCM(
                            senderId.toString(),
                            content,
                            new FCMData.CHAT(
                                    senderName
                                    , chatRoom.getId()
                                    , chatRoom.getType()
                                    , type
                            )
                    );
                    fcmService.sendFCM(authInfo.fcmToken(), fcmData);
                }
            }
        }
    }
    // 메시지 보내기 + redis 저장
    public void saveMessage(Long senderId, Long chatRoomId, Object content, ChatMessage.MessageType type, LocalDateTime now) {
        Long id = chatRepository.getMessageId();
        Integer unreadCount = stomphandler.getOfflineMemberCount(chatRoomId);
        ChatMessage message = new ChatMessage(id, type, senderId,
                chatRoomId, content, unreadCount,
                now, now);
        // 메시지 소켓 전달
        ChatMessageResponse.MESSAGE<String> responseMessage = ChatMessageResponse.MESSAGE.of(message);

        messagingTemplate.convertAndSend("/api/sub/" + chatRoomId, responseMessage);
        chatRepository.save(message);

    }

    public String sendFile(Long senderId, Long chatRoomId, Long fileId, LocalDateTime now) {
        // fileId로 preSignedUrl가져와서 보내기
        FileResponse.DOWNLOAD fileResponse = fileService.downloadFile(fileId);

        ChatRoom chatroom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        chatroom.getFileIdList().add(fileId);

        Map<String, Object> sendContent = new HashMap<>();
        sendContent.put("fileId", fileId);
        sendContent.put("preSignedUrl", fileResponse.presignedUrl());
        saveMessage(senderId, chatRoomId, sendContent, ChatMessage.MessageType.FILE, now);

        return fileResponse.presignedUrl();
    }
    // 채팅방 상태 변경
    public void updateChatRoomType(Long chatRoomId, ChatRoom.ChatRoomType type) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);

        chatRoom.updateType(type);
        chatRoomRepository.save(chatRoom);
        List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoomId(chatRoomId);
        for (ChatRoomMember member : members) {
            member.updateChatRoomType(type);
            chatRoomMemberRepository.save(member);
        }
    }

    // 채팅방 만들기
    @Transactional
    public Long makeChatRoom(ChatRoom.ChatRoomType type, List<Long> memberIdList) {
        log.info("chatroom created");
        ChatRoom chatRoom = new ChatRoom(type);
        chatRoomRepository.save(chatRoom);

        log.info("chatroom member join start");
        for (Long memberId : memberIdList) {
            joinChatRoom(chatRoom.getId(), memberId);
        }
        if (type == ChatRoom.ChatRoomType.MATCH)
            scheduleRoomTermination(chatRoom.getId(), 5, TimeUnit.MINUTES);

        return chatRoom.getId();
    }

    @Transactional
    public void joinChatRoom(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        // 채팅방에 있는지 확인
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberIdAndChatRoomId(memberId, chatRoomId)
                .orElse(null);
        // 채팅방에 이미 있거나 나간 상태 일때
        if (chatRoomMember != null) {
            // 이미 있다면 Pass
            if(chatRoomMember.isJoined())
                return;
            chatRoomMember.updateIsJoined(true);
        }
        // 새로운 채팅방 맴버일 때
        else {
            // 채팅방 맴버 생성
            Member member = memberRepository.findById(memberId).orElse(null);
            if (member == null)
                return;
            chatRoomMember = new ChatRoomMember(member, chatRoom);
        }
        // 친구 채팅이 아닐 경우 랜덤 이름 생성
        if (chatRoom.getType() != ChatRoom.ChatRoomType.FRIEND) {
            chatRoomMember.updateDisplayName(randomUserNameService.getRandomUserName());
        }
        //  입장 알림
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        saveMessage(0L, chatRoomId, chatRoomMember.getUserName(), ChatMessage.MessageType.USER_JOIN, now);

        // 채팅방 목록 추가
        chatRoomMemberRepository.save(chatRoomMember);
        chatRoom.getMemberIdList().add(memberId);
        stomphandler.setMemberOffline(chatRoom.getId(), memberId);
    }

    @Transactional
    public void leaveChatRoom(Long chatRoomId, Long memberId) {
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberIdAndChatRoomId(memberId, chatRoomId).orElseThrow(ChatRoomMemberNotFoundException::new);
        ChatRoom chatRoom = chatRoomMember.getChatRoom();
        // 채팅방 인원 변경
        chatRoom.getMemberIdList().remove(memberId);
        // session에서 삭제
        stomphandler.setMemberOnline(chatRoomId, memberId);
        // entity 삭제
        chatRoomMemberRepository.delete(chatRoomMember);

        // 채팅방 인원이 없으면
        if (chatRoom.getMemberIdList().isEmpty()) {
            chatRoomRepository.delete(chatRoom);
            LocalChat localChat = localChatRepository.findByChatRoomId(chatRoomId).orElse(null);
            // 장소 채팅 삭제
            if (localChat != null) {
                localChatRepository.delete(localChat);
                geoHashService.delete(LocalChatService.REDIS_KEY, String.valueOf(localChat.getId()));
            }
            chatRepository.removeChatRoom(chatRoomId);
        }
        // 퇴장 알림
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        saveMessage(0L, chatRoomId, chatRoomMember.getUserName(), ChatMessage.MessageType.USER_LEAVE, now);
    }

    // 채팅방 목록 요청
    public List<ChatRoomResponse.CHATROOM> getChatRoomList(Long memberId) {
        List<ChatRoomMember> chatroomMembers = chatRoomMemberRepository.findByMemberId(memberId);
        return chatroomMembers.stream()
                .filter(ChatRoomMember::isJoined)
                .map(chatRoomMember -> {
                    ChatRoom chatRoom = chatRoomMember.getChatRoom();
                    ChatMessage recentMessage = chatRepository.getLatestMessage(chatRoom.getId(),memberId);

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

    // 채팅방 맴버 목록 조회
    private List<MemberInfo> getChatRoomMembers(ChatRoom chatRoom) {
        List<MemberInfo> list = new ArrayList<>();
        for (ChatRoomMember member : chatRoom.getMembers()) {
            if (chatRoom.getType() == ChatRoom.ChatRoomType.FRIEND) {
                list.add(MemberInfo.of(member.getMember()));
            } else {
                list.add(new MemberInfo(member.getMember().getId(), member.getDisplayName(), 0L));
            }
        }
        return list;
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


    // 채팅방 5분 스케줄러
    private void scheduleRoomTermination(Long chatRoomId, long delay, TimeUnit unit) {
        scheduler.schedule(() -> {
            log.info("timeout roomId {}", chatRoomId);
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
            log.info("chatroom type {}", chatRoom.getType());
            if (chatRoom == null) {
                return;
            }
            if (chatRoom.getType().equals(ChatRoom.ChatRoomType.MATCH)) {
                // 채팅방의 상태를 대기 상태로 변경
                updateChatRoomType(chatRoomId, ChatRoom.ChatRoomType.WAITING);
                ChatMessageRequest.SEND req = new ChatMessageRequest.SEND(ChatMessage.MessageType.TIMEOUT, "5분이 지났습니다.\n대화를 이어가려면 친구요청을 보내보세요.");
                sendMessage(0L, chatRoomId, req);
            }
        }, delay, unit);
    }
}

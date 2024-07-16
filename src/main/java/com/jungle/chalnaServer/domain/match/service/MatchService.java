package com.jungle.chalnaServer.domain.match.service;

import com.jungle.chalnaServer.domain.auth.domain.entity.AuthInfo;
import com.jungle.chalnaServer.domain.auth.repository.AuthInfoRepository;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chat.exception.ChatRoomMemberNotFoundException;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import com.jungle.chalnaServer.domain.chat.repository.ChatRoomMemberRepository;
import com.jungle.chalnaServer.domain.chat.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.chat.service.ChatService;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchRequest;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchResponse;
import com.jungle.chalnaServer.domain.match.domain.dto.SendStatus;
import com.jungle.chalnaServer.domain.match.domain.entity.MatchNotification;
import com.jungle.chalnaServer.domain.match.domain.entity.MatchNotificationStatus;
import com.jungle.chalnaServer.domain.match.exception.NotificationNotFoundException;
import com.jungle.chalnaServer.domain.match.repository.MatchNotificationRepository;
import com.jungle.chalnaServer.domain.relation.domain.dto.RelationResponse;
import com.jungle.chalnaServer.domain.relation.domain.entity.FriendStatus;
import com.jungle.chalnaServer.domain.relation.domain.entity.Relation;
import com.jungle.chalnaServer.domain.relation.domain.entity.RelationPK;
import com.jungle.chalnaServer.domain.relation.service.RelationService;
import com.jungle.chalnaServer.global.common.entity.MessageType;
import com.jungle.chalnaServer.global.common.repository.DeviceInfoRepository;
import com.jungle.chalnaServer.infra.fcm.FCMService;
import com.jungle.chalnaServer.infra.fcm.dto.FCMData;
import com.jungle.chalnaServer.infra.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jungle.chalnaServer.domain.match.domain.entity.MatchNotificationStatus.SEND;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {
    private final FCMService fcmService;
    private final ChatService chatService;
    private final RelationService relationService;
    private final FileService fileService;

    private final MatchNotificationRepository matchNotiRepository;
    private final AuthInfoRepository authInfoRepository;
    private final DeviceInfoRepository deviceInfoRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public List<MatchResponse.SEND_INFO> matchMessageSend(MatchRequest.Send dto, Long senderId) {

        List<MatchResponse.SEND_INFO> sendInfos = new ArrayList<>();
        MessageType contentType = dto.getContentType();
        String content;
        FCMData.CONTENT message;

        if (contentType == MessageType.FILE) {
            content = dto.getContent();
            message = FCMData.CONTENT.file(fileService.downloadFile(Long.valueOf(dto.getContent())).presignedUrl());
        } else {
            content = dto.getContent();
            message = FCMData.CONTENT.message(content);
        }

        for (String deviceId : dto.getDeviceIdList()) {
            SendStatus status = processDeviceId(deviceId, senderId, contentType, content, message);
            sendInfos.add(new MatchResponse.SEND_INFO(deviceId, status));
        }

        return sendInfos;
    }

    private SendStatus processDeviceId(String deviceId, Long senderId, MessageType contentType, String content, FCMData.CONTENT message) {
        // 수신자 조회
        Long receiverId = deviceInfoRepository.findById(deviceId);
        if (receiverId == null) {
            return SendStatus.FAIL;
        }

        // 수신자 인증정보 조회
        AuthInfo authInfo = authInfoRepository.findById(receiverId);
        if (authInfo == null) {
            return SendStatus.FAIL;
        }

        // 차단 여부 조회
        RelationPK pk = new RelationPK(senderId, receiverId);
        Relation relation = relationService.findRelation(pk);
        Relation reverse = relationService.findRelation(pk.reverse());
        if (relation.isBlocked() || reverse.isBlocked()) {
            return SendStatus.FAIL;
        }

        // 친구 여부 조회
        if (relation.getFriendStatus().equals(FriendStatus.ACCEPTED)) {
            return SendStatus.FRIEND;
        }

        // 인연 채팅 중 확인
        ChatRoom chatRoom = relation.getChatRoom();
        if (chatRoom != null && chatRoom.getType().equals(ChatRoom.ChatRoomType.MATCH)) {
            return SendStatus.MATCHING;
        }
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        // 이미 보낸 인연 있는지 확인
        if(matchNotiRepository.existsBySenderIdAndReceiverIdAndDeleteAtIsAfter(senderId,receiverId,now))
            return SendStatus.SEND;


        // 매치 생성
        MatchNotification matchNotification = MatchNotification.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .message(content)
                .messageType(contentType)
                .status(MatchNotificationStatus.SEND)
                .deleteAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusMinutes(5L))
                .build();

        matchNotiRepository.save(matchNotification);

        // 푸시알림 전송
        fcmService.sendFCM(
                authInfo.fcmToken(),
                FCMData.instanceOfMatchFCM(
                        senderId.toString(),
                        message,
                        new FCMData.MATCH(
                                matchNotification.getId(),
                                relation.getOverlapCount(),
                                receiverId)
                )
        );

        return SendStatus.SUCCESS;
    }

    @Transactional
    public Map<String, String> matchAccept(Long notificationId) {
        MatchNotification matchNotification = matchNotiRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        if (matchNotification.getStatus() != SEND) return MatchResponse.MatchReject("이미 처리된 요청입니다.");

        // 인연 메시지 전송
        Long chatRoomId = sendMatchMessage(
                matchNotification.getSenderId(),
                matchNotification.getReceiverId(),
                matchNotification.getMessage(),
                matchNotification.getMessageType()
        );

        // matchNotification 저장
        matchNotification.updateStatus(MatchNotificationStatus.ACCEPT);

        return MatchResponse.MatchAccept(Long.toString(chatRoomId));
    }

    @Transactional
    public Long sendMatchMessage(Long senderId,Long receiverId,String message,MessageType messageType){

        RelationPK pk = new RelationPK(senderId, receiverId);
        // 1:1 채팅방 탐색
        Relation relation = relationService.findRelation(pk);
        Relation reverse = relationService.findRelation(pk.reverse());
        ChatRoom chatRoom = relation.getChatRoom();
        Long chatRoomId;
        boolean created = false;

        if (chatRoom != null) {
            chatRoomId = chatRoom.getId();
            // 채팅방 맴버 참여
            chatService.joinChatRoom(chatRoomId,senderId);
            chatService.joinChatRoom(chatRoomId,receiverId);
            // 친구가 아닐 때는 MATCH 갱신
            if (!chatRoom.getType().equals(ChatRoom.ChatRoomType.FRIEND)) {
                chatService.updateChatRoomType(chatRoomId, ChatRoom.ChatRoomType.MATCH);
                chatService.rescheduleRoomTermination(chatRoomId, 5, TimeUnit.MINUTES);
            }
        }else{
            // 채팅방 생성
            chatRoomId = chatService.makeChatRoom(ChatRoom.ChatRoomType.MATCH, List.of(senderId, receiverId));
            chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(NotificationNotFoundException::new);
            // 채팅방 연결
            relation.updateChatRoom(chatRoom);
            reverse.updateChatRoom(chatRoom);
            created = true;
        }
        // 채팅 메시지 생성
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        if (messageType.equals(MessageType.FILE))
            chatService.sendFile(senderId, chatRoomId, Long.valueOf(message), now);
        else chatService.saveMessage(senderId, chatRoomId, message, ChatMessage.MessageType.CHAT, now);

        // 보낸사람 읽음 처리
        chatRepository.readLastChatMessage(chatRoomId);

        // fcm 메시지 구성
        AuthInfo senderInfo = authInfoRepository.findById(senderId);
        ChatRoomMember sender = chatRoomMemberRepository.findByMemberIdAndChatRoomId(receiverId, chatRoomId)
                .orElseThrow(ChatRoomMemberNotFoundException::new);
        ChatRoomMember receiver = chatRoomMemberRepository.findByMemberIdAndChatRoomId(receiverId, chatRoomId)
                .orElseThrow(ChatRoomMemberNotFoundException::new);

        FCMData.CONTENT fcmMessage = FCMData.CONTENT.message(
                created ?
                        sender.getUserName() + "님이 인연 메시지를 보냈습니다."
                        :
                        "새로운 인연과의 대화가 시작됐습니다."
        );
        log.info("push alarm {}", fcmMessage.content());
        // 푸시알림 전송
        fcmService.sendFCM(senderInfo.fcmToken(),
                FCMData.instanceOfChatFCM(senderId.toString(),
                        fcmMessage,
                        new FCMData.CHAT(
                                receiver.getUserName(),
                                chatRoomId,
                                ChatRoom.ChatRoomType.MATCH,
                                ChatMessage.MessageType.CHAT
                        )
                ));

        return chatRoomId;
    }


    public Map<String, String> matchReject(Long notificationId) {
        MatchNotification matchNotification = matchNotiRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        if (matchNotification.getStatus() != SEND) return MatchResponse.MatchReject("이미 처리된 요청입니다.");

        matchNotification.updateStatus(MatchNotificationStatus.REJECT);
        matchNotiRepository.save(matchNotification);

        return MatchResponse.MatchReject("요청이 처리되었습니다.");
    }


    public List<Map<String, String>> matchList(Long receiverId) {
        List<MatchNotification> notifications = matchNotiRepository.findByReceiverId(receiverId);

        return notifications.stream()
                .filter(notification -> notification.getDeleteAt() == null || notification.getDeleteAt().isAfter(LocalDateTime.now(ZoneId.of("Asia/Seoul"))))
                .map(notification -> {
                    Long senderId = notification.getSenderId();
                    RelationResponse relationResponse = relationService.findByOtherId(receiverId, senderId);

                    Map<String, String> map = Map.of(
                            "notificationId", notification.getId().toString(),
                            "senderId", senderId.toString(),
                            "message", notification.getMessage(),
                            "createAt", notification.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            "overlapCount", relationResponse.overlapCount().toString()
                    );
                    return map;
                })
                .collect(Collectors.toList());
    }


    public Map<String, String> matchAllReject(Long receiverId) {
        List<MatchNotification> notifications = matchNotiRepository.findByReceiverId(receiverId);

        notifications.forEach(notification -> {
            notification.updateStatus(MatchNotificationStatus.REJECT);
            matchNotiRepository.save(notification);
        });

        return MatchResponse.MatchReject("요청이 처리되었습니다.");
    }
}

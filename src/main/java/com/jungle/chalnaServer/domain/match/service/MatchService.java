package com.jungle.chalnaServer.domain.match.service;

import com.jungle.chalnaServer.domain.auth.domain.entity.AuthInfo;
import com.jungle.chalnaServer.domain.auth.repository.AuthInfoRepository;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chat.exception.ChatRoomMemberNotFoundException;
import com.jungle.chalnaServer.domain.chat.repository.ChatRoomMemberRepository;
import com.jungle.chalnaServer.domain.chat.service.ChatService;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchRequest;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchResponse;
import com.jungle.chalnaServer.domain.match.domain.entity.MatchNotification;
import com.jungle.chalnaServer.domain.match.domain.entity.MatchNotificationStatus;
import com.jungle.chalnaServer.domain.match.exception.NotificationNotFoundException;
import com.jungle.chalnaServer.domain.match.repository.MatchNotificationRepository;
import com.jungle.chalnaServer.domain.relation.domain.dto.RelationResponse;
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
import java.util.List;
import java.util.Map;
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

    public MatchResponse.MESSAGE_SEND matchMessageSend(MatchRequest.Send dto, Long senderId) {

        List<String> deviceIdList = dto.getDeviceIdList();
        int sendCount = 0;

        MessageType contentType = dto.getContentType();
        String content;
        FCMData.CONTENT message;
        if (contentType == MessageType.FILE) {
            content = dto.getContent();
            message = FCMData.CONTENT.file(fileService.downloadFile(Long.valueOf(dto.getContent())).presignedUrl());
        }
        else {
            content = dto.getContent();
            message = FCMData.CONTENT.message(content);
        }


        for (String deviceId : deviceIdList) {
            //deviceIdList에서 하나씩 찾아서 바꿔주기
            Long receiverId = deviceInfoRepository.findById(deviceId);
            if (receiverId == null) {
                continue;
            }

            AuthInfo authInfo = authInfoRepository.findById(receiverId);
            if (authInfo == null)
                continue;

            RelationResponse relation = relationService.findByOtherId(receiverId, senderId);
            RelationResponse reverse = relationService.findByOtherId(senderId, receiverId);
            if (relation.isBlocked() || reverse.isBlocked())
                continue;

            MatchNotification matchNotification = MatchNotification.builder()
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .message(content)
                    .messageType(contentType)
                    .status(MatchNotificationStatus.SEND)
                    .deleteAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusMinutes(10L))
                    .build();

            matchNotiRepository.save(matchNotification);

            fcmService.sendFCM(
                    authInfo.fcmToken(),
                    FCMData.instanceOfMatchFCM(
                            senderId.toString(),
                            message,
                            new FCMData.MATCH(
                                    matchNotification.getId(),
                                    relation.overlapCount(),
                                    receiverId)
                    )
            );
            sendCount++;
        }

        return new MatchResponse.MESSAGE_SEND(sendCount);
    }

    @Transactional
    public Map<String, String> matchAccept(Long notificationId) {
        MatchNotification matchNotification = matchNotiRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        if (matchNotification.getStatus() != SEND) return MatchResponse.MatchReject("이미 처리된 요청입니다.");

        // matchNotification 저장
        matchNotification.updateStatus(MatchNotificationStatus.ACCEPT);

        List<Long> memberIdList = List.of(
                matchNotification.getSenderId(),
                matchNotification.getReceiverId()
        );
        Long chatRoomId = chatService.makeChatRoom(ChatRoom.ChatRoomType.MATCH, memberIdList);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        if (matchNotification.getMessageType().equals(MessageType.FILE)) {
            FCMData.CONTENT.file(chatService.sendFile(matchNotification.getSenderId(), chatRoomId,Long.valueOf(matchNotification.getMessage()),now));
        } else {
            chatService.saveMessage(chatRoomId, matchNotification.getSenderId(), matchNotification.getMessage(), ChatMessage.MessageType.CHAT,now);
        }

        ChatRoomMember sender = chatRoomMemberRepository.findByMemberIdAndChatRoomId(matchNotification.getSenderId(), chatRoomId)
                .orElseThrow(ChatRoomMemberNotFoundException::new);
        AuthInfo senderInfo = authInfoRepository.findById(matchNotification.getSenderId());

        fcmService.sendFCM(senderInfo.fcmToken(),
                FCMData.instanceOfChatFCM(
                        matchNotification.getSenderId().toString(),
                        FCMData.CONTENT.message("인연과의 대화가 시작됐습니다."),
                        new FCMData.CHAT(
                        sender.getUserName(),
                                chatRoomId,
                                ChatRoom.ChatRoomType.MATCH,
                                ChatMessage.MessageType.CHAT
                        )
        ));

        return MatchResponse.MatchAccept(Long.toString(chatRoomId));
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

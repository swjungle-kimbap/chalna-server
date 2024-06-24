package com.jungle.chalnaServer.domain.match.service;

import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.service.ChatRoomService;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchRequest;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchResponse;
import com.jungle.chalnaServer.domain.match.domain.entity.MatchNotification;
import com.jungle.chalnaServer.domain.match.domain.entity.MatchNotificationStatus;
import com.jungle.chalnaServer.domain.match.exception.NotificationNotFoundException;
import com.jungle.chalnaServer.domain.match.repository.MatchNotificationRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.domain.relation.domain.dto.RelationResponse;
import com.jungle.chalnaServer.domain.relation.service.RelationService;
import com.jungle.chalnaServer.infra.fcm.FCMService;
import com.jungle.chalnaServer.infra.fcm.dto.FCMData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jungle.chalnaServer.domain.match.domain.entity.MatchNotificationStatus.ACCEPT;
import static com.jungle.chalnaServer.domain.match.domain.entity.MatchNotificationStatus.SEND;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MemberRepository memberRepository;
    private final MatchNotificationRepository matchNotiRepository;
    private final ChatRoomService chatRoomService;
    private final ChatRepository chatRepository;
    private final RelationService relationService;

    public Map<String, String> matchMessageSend(MatchRequest.Send dto, Long senderId) throws Exception {
        Member member = memberRepository.findById(senderId)
                .orElseThrow(MemberNotFoundException::new);

        Long receiverId = Long.parseLong(dto.getReceiverId());
        List<String> interestTag = dto.getInterestTag(); // tag 처리 추후 보완

        RelationResponse relationResponse = relationService.findByOtherId(receiverId, senderId);


        Member receiver = memberRepository.findById(receiverId).orElseThrow(MemberNotFoundException::new);

        if (relationService.findByOtherId(receiverId, senderId).isBlocked()
                || relationService.findByOtherId(senderId, receiverId).isBlocked())
            return MatchResponse.MatchMessageSend("차단된 유저 입니다.");

        String fcmToken = receiver.getFcmToken();

        MatchNotification matchNotification = MatchNotification.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .message(dto.getMessage())
                .status(MatchNotificationStatus.SEND)
                .deleteAt(LocalDateTime.now().plusMinutes(10L))
                .build();

        matchNotiRepository.save(matchNotification);

        FCMService.sendFCM(fcmToken, FCMData.instanceOfMatchFCM(senderId.toString(), dto.getMessage(), LocalDateTime.now().toString()));

        return MatchResponse.MatchMessageSend("인연 요청을 처리했습니다.");
    }

    public Map<String, String> matchAccept(Long notificationId) throws Exception {
        MatchNotification matchNotification = matchNotiRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        if (matchNotification.getStatus() != SEND) return MatchResponse.MatchReject("이미 처리된 요청입니다.");

        // matchNotification 저장
        matchNotification.updateStatus(MatchNotificationStatus.ACCEPT);
        matchNotiRepository.save(matchNotification);

        if (matchNotification.getStatus() != ACCEPT) return MatchResponse.MatchReject("수락 처리 오류 입니다.");

        List<Long> memberIdList = new ArrayList<>();
        memberIdList.add(matchNotification.getSenderId());
        memberIdList.add(matchNotification.getReceiverId());
        Long chatRoomId = chatRoomService.makeChatRoom(ChatRoom.ChatRoomType.MATCH, 2, memberIdList);

        // Redis 저장
        Long chatId = chatRepository.makeMessageId();

        ChatMessage message = new ChatMessage(chatId,
                ChatMessage.MessageType.CHAT,
                matchNotification.getSenderId(),
                chatRoomId,
                matchNotification.getMessage(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now());

        chatRepository.saveMessage(message);

        // sender push 알림 추가
        Member sender = memberRepository.findById(matchNotification.getSenderId()).orElseThrow(MemberNotFoundException::new);
        String fcmToken = sender.getFcmToken();

        Member receiver = memberRepository.findById(matchNotification.getReceiverId()).orElseThrow(MemberNotFoundException::new);


        FCMService.sendFCM(fcmToken, FCMData.instanceOfChatFCM(matchNotification.getReceiverId().toString(),
                "인연과의 대화가 시작됐습니다.",
                LocalDateTime.now().toString(),
                receiver.getUsername(),
                chatRoomId.toString(),
                ChatMessage.MessageType.CHAT.toString()));

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
                .filter(notification -> notification.getDeleteAt() == null || notification.getDeleteAt().isAfter(LocalDateTime.now()))
                .map(notification -> {
                    Long senderId = notification.getSenderId();
                    RelationResponse relationResponse = relationService.findByOtherId(receiverId, senderId);

                    Map<String, String> map = Map.of(
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

    //아직 미사용 코드
//    public Map<String, String> matchCommon(MatchRequest.Send dto, Long senderId) throws Exception {
//        Member member = memberRepository.findById(senderId)
//                .orElseThrow(MemberNotFoundException::new);
//
//        Long receiverId = Long.parseLong(dto.getReceiverId());
//        Map<String, String> result = null;
//
//        String message = dto.getMessage();
//        if (message != null && !message.isEmpty()) {
//            result = matchMessageSend(dto, senderId);
//        }
//
//        matchOverlapCountUp(senderId, receiverId);
//        return MatchResponse.MatchMessageSend("match 요청 처리했습니다.");
//    }
//
//    public void matchOverlapCountUp(Long senderId, Long receiverId) throws MatchOverlapFailedException {
//        RelationPK senderPK = new RelationPK(senderId, receiverId);
//        RelationPK receiverPK = new RelationPK(receiverId, senderId);
//
//
//        Optional<Relation> senderRelationOpt = relationRepository.findByRelationPK(senderPK);
//        Optional<Relation> receiverRelationOpt = relationRepository.findByRelationPK(receiverPK);
//
//
//        senderRelationOpt.ifPresent(senderRelation -> {
//            senderRelation.increaseOverlapCount();
//            relationRepository.save(senderRelation);
//        });
//
//        receiverRelationOpt.ifPresent(receiverRelation -> {
//            receiverRelation.increaseOverlapCount();
//            relationRepository.save(receiverRelation);
//        });
//    }
}

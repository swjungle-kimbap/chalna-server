package com.jungle.chalnaServer.domain.match.service;

import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.service.ChatRoomService;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchRequest;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchResponse;
import com.jungle.chalnaServer.domain.match.domain.entity.MatchNotification;
import com.jungle.chalnaServer.domain.match.domain.entity.MatchNotificationStatus;
import com.jungle.chalnaServer.domain.match.exception.MatchOverlapFailedException;
import com.jungle.chalnaServer.domain.match.exception.NotificationNotFoundException;
import com.jungle.chalnaServer.domain.match.repository.MatchNotificationRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.domain.relation.domain.entity.Relation;
import com.jungle.chalnaServer.domain.relation.domain.entity.RelationPK;
import com.jungle.chalnaServer.domain.relation.repository.RelationRepository;
import com.jungle.chalnaServer.infra.fcm.FCMService;
import com.jungle.chalnaServer.infra.fcm.dto.FCMData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jungle.chalnaServer.domain.match.domain.entity.MatchNotificationStatus.ACCEPT;
import static com.jungle.chalnaServer.domain.match.domain.entity.MatchNotificationStatus.SEND;

@Service
public class MatchService {
    private final MemberRepository memberRepository;
    private final MatchNotificationRepository matchNotiRepository;
    private final ChatRoomService chatRoomService;
    private final ChatRepository chatRepository;
    private final RelationRepository relationRepository;

    public MatchService(MemberRepository memberRepository,
                        MatchNotificationRepository matchNotiRepository,
                        ChatRoomService chatRoomService,
                        ChatRepository chatRepository,
                        RelationRepository relationRepository) {
        this.memberRepository = memberRepository;
        this.matchNotiRepository = matchNotiRepository;
        this.chatRoomService = chatRoomService;
        this.chatRepository = chatRepository;
        this.relationRepository = relationRepository;
    }



    public Map<String, String> matchMessageSend(MatchRequest.Send dto, Long senderId) throws Exception {
        Member member = memberRepository.findById(senderId)
                .orElseThrow(MemberNotFoundException::new);

        Long receiverId = Long.parseLong(dto.getReceiverId());
        List<String> interestTag = dto.getInterestTag(); // tag 처리 추후 보완

        Member receiver = memberRepository.findById(receiverId).orElseThrow(MemberNotFoundException::new);
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

    public Map<String, String> matchAccept(Long notificationId) {
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
        return MatchResponse.MatchReject(Long.toString(chatRoomId));
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
                    Map<String, String> map = Map.of(
                            "senderId", notification.getSenderId().toString(),
                            "message", notification.getMessage(),
                            "createAt", notification.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    );
                    return map;
                })
                .collect(Collectors.toList());
    }



    //아직 미사용 코드
    public Map<String, String> matchCommon(MatchRequest.Send dto, Long senderId) throws Exception {
        Member member = memberRepository.findById(senderId)
                .orElseThrow(MemberNotFoundException::new);

        Long receiverId = Long.parseLong(dto.getReceiverId());
        Map<String, String> result = null;

        String message = dto.getMessage();
        if (message != null && !message.isEmpty()) {
            result = matchMessageSend(dto, senderId);
        }

        matchOverlapCountUp(senderId, receiverId);
        return MatchResponse.MatchMessageSend("match 요청 처리했습니다.");
    }

    public void matchOverlapCountUp(Long senderId, Long receiverId) throws MatchOverlapFailedException {
        RelationPK senderPK = new RelationPK(senderId, receiverId);
        RelationPK receiverPK = new RelationPK(receiverId, senderId);


        Optional<Relation> senderRelationOpt = relationRepository.findByRelationPK(senderPK);
        Optional<Relation> receiverRelationOpt = relationRepository.findByRelationPK(receiverPK);


        senderRelationOpt.ifPresent(senderRelation -> {
            senderRelation.increaseOverlapCount();
            relationRepository.save(senderRelation);
        });

        receiverRelationOpt.ifPresent(receiverRelation -> {
            receiverRelation.increaseOverlapCount();
            relationRepository.save(receiverRelation);
        });
    }
}

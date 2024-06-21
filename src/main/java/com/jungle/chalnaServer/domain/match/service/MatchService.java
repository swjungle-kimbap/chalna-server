package com.jungle.chalnaServer.domain.match.service;

import com.jungle.chalnaServer.domain.match.domain.dto.MatchRequest;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchResponse;
import com.jungle.chalnaServer.domain.match.domain.entity.MatchNotification;
import com.jungle.chalnaServer.domain.match.domain.entity.MatchNotificationStatus;
import com.jungle.chalnaServer.domain.match.repository.MatchNotificationRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.infra.fcm.FCMService;
import com.jungle.chalnaServer.infra.fcm.dto.FCMData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private final MemberRepository memberRepository;
    private final MatchNotificationRepository matchNotiRepository;

    public MatchService(MemberRepository memberRepository, MatchNotificationRepository matchNotiRepository) {
        this.memberRepository = memberRepository;
        this.matchNotiRepository = matchNotiRepository;
    }

    public MatchResponse matchMessageSend(MatchRequest.Send dto, Long senderId) throws Exception {
        Member member = memberRepository.findById(senderId)
                .orElseThrow(MemberNotFoundException::new);

        List<String> receiverList = dto.getReceiverList();
        List<String> interestTag = dto.getInterestTag(); // tag 처리 추후 보완

        List<Long> receiverIds = receiverList.stream()
                .map(Long::parseLong)
                .toList();

        List<Member> receivers = receiverIds.stream()
                .map(id -> memberRepository.findById(id).orElse(null))
                .toList();

        List<Member> validReceivers = receivers.stream()
                .filter(receiver -> receiver != null && receiver.getFcmToken() != null && !receiver.getFcmToken().isEmpty())
                .toList();

        for (Member receiver : validReceivers) {
            Long receiverId = receiver.getId();
            String fcmToken = receiver.getFcmToken();

            MatchNotification matchNotification = MatchNotification.builder()
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .message(dto.getMessage())
                    .status(MatchNotificationStatus.SEND)
                    .build();

            matchNotiRepository.save(matchNotification);
            FCMService.sendFCM(fcmToken, FCMData.instanceOfMatchFCM(senderId.toString(), dto.getMessage(), LocalDateTime.now().toString()));
        }

        return MatchResponse.of("인연 보내기 성공");
    }

}

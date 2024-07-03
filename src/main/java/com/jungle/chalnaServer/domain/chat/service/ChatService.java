package com.jungle.chalnaServer.domain.chat.service;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageRequest;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.handler.StompHandler;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chatRoom.exception.ChatRoomNotFoundException;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.infra.fcm.FCMService;
import com.jungle.chalnaServer.infra.fcm.dto.FCMData;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@Service
@Log4j2
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final StompHandler stomphandler;

    private final MemberRepository memberRepository;

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    // 채팅 보내기(+push 알림)
    public void sendMessage(Long memberId, Long roomId, ChatMessageRequest.SEND req) {

        log.info("sendMessage");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        // push 알림 보내기. 채팅룸에 멤버 정보를 확인해서 다른 멤버가 채팅방에 없는 경우 알림 보내기
        if (stomphandler.getOfflineMemberCount(roomId.toString()) > 0) {
            ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
            log.info("offline count {}", stomphandler.getOfflineMemberCount(roomId.toString()));
            Set<Long> offlineMembers = stomphandler.getOfflineMembers(roomId); // 오프라인 유저 정보
            Set<ChatRoomMember> members = chatRoom.getMembers();
            for (ChatRoomMember chatRoomMember : members) {
                Member member = chatRoomMember.getMember();
                if(offlineMembers.contains(member.getId())) {
                    log.info("fcm send to {}",member.getId());
                    FCMData fcmData = FCMData.instanceOfChatFCM(
                            memberId.toString(),
                            req.content(),
                            chatRoomMember.getUserName(),
                            roomId.toString(),
                            chatRoom.getType().toString(),
                            req.type().toString());
                    FCMService.sendFCM(member.getFcmToken(), fcmData);
                }
            }
        }

        sendAndSaveMessage(roomId, memberId, req.content(), req.type(),now);
    }

    // 메시지 보내기 + redis 저장
    public void sendAndSaveMessage(Long chatRoomId, Long senderId, String content, ChatMessage.MessageType type,LocalDateTime now) {
        Long id = chatRepository.getMessageId();
        Integer unreadCount = stomphandler.getOfflineMemberCount(chatRoomId.toString());
        ChatMessage message = new ChatMessage(id, type, senderId,
                chatRoomId, content, unreadCount,
                now, now);
        // 메시지 소켓 전달
        ChatMessageResponse.MESSAGE<String> responseMessage = ChatMessageResponse.MESSAGE.of(message);

        messagingTemplate.convertAndSend("/api/sub/" + chatRoomId, responseMessage);
        chatRepository.save(message);

    }


}

package com.jungle.chalnaServer.domain.chat.service;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageRequest;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.handler.Stomphandler;
import com.jungle.chalnaServer.domain.chat.handler.WebSocketEventListener;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomMemberRepository;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.infra.fcm.FCMService;
import com.jungle.chalnaServer.infra.fcm.dto.FCMData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Stomphandler stomphandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    // 채팅 보내기(+push 알림)
    public void sendMessage(Long memberId, Long roomId, ChatMessageRequest requestMessage, String username){

        log.info("sendMessage");
        // push 알림 보내기. 채팅룸에 멤버 정보를 확인해서 다른 멤버가 채팅방에 없는 경우 알림 보내기
        if (stomphandler.getOfflineUserCount(roomId.toString()) > 0){
            log.info("offline count {}", stomphandler.getOfflineUserCount(roomId.toString()));
            Optional<ChatRoom> chatRoom = chatRoomRepository.findById(roomId);
            Set<String> members = stomphandler.getOfflineUsers(roomId.toString()); // 오프라인 유저 정보

            for (String id : members) {
                log.info("memberId {}", id);
                Member member = memberRepository.findById(Long.parseLong(id)).orElse(null);
                FCMData fcmData = FCMData.instanceOfChatFCM(memberId.toString(), requestMessage.getContent(), LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString(), username, roomId.toString(), chatRoom.get().getType().toString());
                FCMService.sendFCM(member.getFcmToken(), fcmData);
                log.info("send push message");
            }
        }

        sendAndSaveMessage(roomId, memberId, requestMessage.getContent(), requestMessage.getType());
    }

    // 메시지 보내기 + redis 저장
    public void sendAndSaveMessage(Long chatRoomId, Long senderId, String content, ChatMessage.MessageType type) {
        Long id = chatRepository.makeMessageId();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        Integer unreadCount = stomphandler.getOfflineUserCount(chatRoomId.toString());

        // 메시지 소켓 전달
        ChatMessageResponse responseMessage = ChatMessageResponse.builder()
                .id(id)
                .content(content)
                .type(type)
                .senderId(senderId)
                .createdAt(now)
                .unreadCount(unreadCount)
                .build();

        messagingTemplate.convertAndSend("/api/sub/" + chatRoomId, responseMessage);

        // Redis에 메시지 저장
        ChatMessage message = new ChatMessage(id, type, senderId,
                chatRoomId, content, unreadCount,
                now, now);

        chatRepository.saveMessage(message);

    }


}

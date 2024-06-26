package com.jungle.chalnaServer.domain.chat.service;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageRequest;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.handler.WebSocketEventListener;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomMemberRepository;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberInfo;
import com.jungle.chalnaServer.infra.fcm.FCMService;
import com.jungle.chalnaServer.infra.fcm.dto.FCMData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Log4j2
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatRoomMemberRepository chatRoomMemberRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WebSocketEventListener webSocketEventListener;

    // 채팅 보내기(+push 알림)
    public void sendMessage(Long memberId, Long roomId, ChatMessageRequest requestMessage, String username){
        Boolean status = true;

        // push 알림 보내기. 채팅룸에 멤버 정보를 확인해서 다른 멤버가 채팅방에 없는 경우 알림 보내기
        if (webSocketEventListener.getConnectedCount(roomId) == 1){
            List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoomIdAndIsRemovedFalse(roomId);
            if (members.size() != 1) {
                for (ChatRoomMember chatRoomMember : members) {
                    if (!chatRoomMember.getMember().getId().equals(memberId)){
                        // push 알림 보내기
                        FCMData fcmData = FCMData.instanceOfChatFCM(memberId.toString(), requestMessage.getContent(), LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString(), username, roomId.toString(), chatRoomMember.getChatRoom().getType().toString());
                        FCMService.sendFCM(chatRoomMember.getMember().getFcmToken(), fcmData);
                        log.info("send push message");
                    }
                }
                status = false;
            }
        }

        sendAndSaveMessage(roomId, memberId, requestMessage.getContent(), requestMessage.getType(), status);
    }

    // 메시지 보내기 + redis 저장
    public void sendAndSaveMessage(Long chatRoomId, Long senderId, String content, ChatMessage.MessageType type, Boolean status) {
        Long id = chatRepository.makeMessageId();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 메시지 소켓 전달
        ChatMessageResponse responseMessage = ChatMessageResponse.builder()
                .id(id)
                .content(content)
                .type(type)
                .senderId(senderId)
                .createdAt(now)
                .status(status)
                .build();

        messagingTemplate.convertAndSend("/topic/" + chatRoomId, responseMessage);

        // Redis에 메시지 저장
        ChatMessage message = new ChatMessage(id, type, senderId,
                chatRoomId, content, status,
                now, now);

        chatRepository.saveMessage(message);

    }


}

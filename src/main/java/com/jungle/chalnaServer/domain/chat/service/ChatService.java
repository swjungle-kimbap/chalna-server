package com.jungle.chalnaServer.domain.chat.service;

import com.jungle.chalnaServer.domain.auth.domain.entity.AuthInfo;
import com.jungle.chalnaServer.domain.auth.repository.AuthInfoRepository;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageRequest;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatMessage;
import com.jungle.chalnaServer.domain.chat.handler.StompHandler;
import com.jungle.chalnaServer.domain.chat.repository.ChatRepository;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chatRoom.exception.ChatRoomNotFoundException;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomRepository;
import com.jungle.chalnaServer.infra.fcm.FCMService;
import com.jungle.chalnaServer.infra.fcm.dto.FCMData;
import com.jungle.chalnaServer.infra.file.domain.dto.FileResponse;
import com.jungle.chalnaServer.infra.file.repository.FileInfoRepository;
import com.jungle.chalnaServer.infra.file.service.FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service
@Log4j2
@RequiredArgsConstructor
public class ChatService {

    private final StompHandler stomphandler;
    private final SimpMessagingTemplate messagingTemplate;

    private final FileService fileService;
    private final FCMService fcmService;

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FileInfoRepository fileInfoRepository;
    private final AuthInfoRepository authInfoRepository;

    @Transactional
    // 채팅 보내기(+push 알림)
    public void sendMessage(Long memberId, Long roomId, ChatMessageRequest.SEND req) {

        log.info("sendMessage");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        FCMData.CONTENT content;

        if (req.type().equals(ChatMessage.MessageType.FILE)) {
            content = FCMData.CONTENT.file(sendFile(memberId, roomId, req,now));
        } else {
            sendAndSaveMessage(roomId, memberId, req.content(), req.type(),now);
            content = FCMData.CONTENT.message(req.content().toString());
        }


        // push 알림 보내기. 채팅룸에 멤버 정보를 확인해서 다른 멤버가 채팅방에 없는 경우 알림 보내기
        if (stomphandler.getOfflineMemberCount(roomId.toString()) > 0) {
            ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
            log.info("offline count {}", stomphandler.getOfflineMemberCount(roomId.toString()));
            Set<Long> offlineMembers = stomphandler.getOfflineMembers(roomId); // 오프라인 유저 정보
            Set<ChatRoomMember> members = chatRoom.getMembers();

            for (ChatRoomMember chatRoomMember : members) {
                Long receiverId = chatRoomMember.getMember().getId();
                if(offlineMembers.contains(receiverId) && !receiverId.equals(memberId)) {
                    log.info("fcm send to {}",receiverId);
                    AuthInfo authInfo = authInfoRepository.findById(receiverId);
                    FCMData fcmData = FCMData.instanceOfChatFCM(
                            memberId.toString(),
                            content,
                            new FCMData.CHAT(
                            chatRoomMember.getUserName(),
                            roomId,
                            chatRoom.getType(),
                            req.type())
                    );
                    fcmService.sendFCM(authInfo.fcmToken(), fcmData);
                }
            }
        }
    }

    // 메시지 보내기 + redis 저장
    public void sendAndSaveMessage(Long chatRoomId, Long senderId, Object content, ChatMessage.MessageType type,LocalDateTime now) {
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

    private String sendFile(Long senderId, Long chatRoomId, ChatMessageRequest.SEND req,LocalDateTime now) {
        Map<String, Object> contentMap = (Map<String, Object>) req.content();
        Long fileId = Long.valueOf(contentMap.get("fileId").toString());

        // fileId로 preSignedUrl가져와서 보내기
        FileResponse.DOWNLOAD fileResponse = fileService.downloadFile(fileId);

        ChatRoom chatroom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        chatroom.getFileIdList().add(fileId);

        Map<String, Object> sendContent = new HashMap<>();
        sendContent.put("fileId", fileId);
        sendContent.put("preSignedUrl", fileResponse.presignedUrl());
        sendAndSaveMessage(chatRoomId, senderId, sendContent, req.type(),now);

        return fileResponse.presignedUrl();
    }


}

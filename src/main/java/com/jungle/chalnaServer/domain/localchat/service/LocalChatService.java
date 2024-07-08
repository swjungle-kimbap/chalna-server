package com.jungle.chalnaServer.domain.localchat.service;

import com.jungle.chalnaServer.domain.chat.service.ChatService;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chat.exception.ChatRoomNotFoundException;
import com.jungle.chalnaServer.domain.chat.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.localchat.domain.dto.LocalChatRequest;
import com.jungle.chalnaServer.domain.localchat.domain.dto.LocalChatResponse;
import com.jungle.chalnaServer.domain.localchat.domain.entity.LocalChat;
import com.jungle.chalnaServer.domain.localchat.exception.LocalChatNotFoundException;
import com.jungle.chalnaServer.domain.localchat.exception.LocalChatNotOwnerException;
import com.jungle.chalnaServer.domain.localchat.exception.LocalChatTooCloseException;
import com.jungle.chalnaServer.domain.localchat.repository.LocalChatRepository;
import com.jungle.chalnaServer.domain.relation.domain.dto.RelationResponse;
import com.jungle.chalnaServer.domain.relation.service.RelationService;
import com.jungle.chalnaServer.global.util.GeoHashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LocalChatService {
    public static final String REDIS_KEY = "LOCAL_CHAT";

    private final GeoHashService geoHashService;
    private final ChatService chatService;
    private final RelationService relationService;

    private final LocalChatRepository localChatRepository;
    private final ChatRoomRepository chatRoomRepository;

    public List<LocalChatResponse.LOCAL_CHAT_INFO> findNearLocalChat(final Long memberId, LocalChatRequest.RADIUS dto) {
        List<Long> ids = geoHashService.radius(REDIS_KEY, new Point(dto.longitude(), dto.latitude()), dto.distance()).stream()
                .map(g -> Long.valueOf(g.name()))
                .toList();
        List<LocalChat> localChats = localChatRepository.findAllById(ids);
        List<LocalChatResponse.LOCAL_CHAT_INFO> localChatResponses = new ArrayList<>();
        for (LocalChat localChat : localChats) {
            boolean isOwner, isJoined;
            isOwner = memberId.equals(localChat.getOwnerId());
            isJoined = localChat.getChatRoom().getMemberIdList().contains(memberId);
            if (!isOwner) {
                RelationResponse relation = relationService.findByOtherId(memberId, localChat.getOwnerId());
                if (relation.isBlocked())
                    continue;
            }
            localChatResponses.add(LocalChatResponse.LOCAL_CHAT_INFO.of(localChat, isOwner, isJoined));
        }
        return localChatResponses;
    }

    public LocalChatResponse.LOCAL_CHAT makeLocalChat(LocalChatRequest.ADD dto, final long ownerId) {

        if (!geoHashService.radius(REDIS_KEY, new Point(dto.longitude(), dto.latitude()), 100.0).isEmpty())
            throw new LocalChatTooCloseException();

        Long chatRoomId = chatService.makeChatRoom(ChatRoom.ChatRoomType.LOCAL, List.of(ownerId));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);

        LocalChat localChat = localChatRepository.save(new LocalChat(ownerId, dto.name(), dto.description(), chatRoom, dto.latitude(), dto.longitude()));
        geoHashService.set(REDIS_KEY, new Point(dto.longitude(), dto.latitude()), String.valueOf(localChat.getId()));

        return LocalChatResponse.LOCAL_CHAT.of(localChat);
    }

    public String removeLocalChat(final Long id, final long ownerId) {
        LocalChat localChat = localChatRepository.findById(id).orElseThrow(LocalChatNotFoundException::new);
        if (localChat.getOwnerId() != ownerId)
            throw new LocalChatNotOwnerException();
        localChat.getChatRoom().updateType(ChatRoom.ChatRoomType.CLOSED);
        localChatRepository.delete(localChat);
        geoHashService.delete(REDIS_KEY, String.valueOf(id));

        return "장소 채팅 삭제 완료.";
    }

    public String joinLocalChat(final Long id, final long joinId) {
        LocalChat localChat = localChatRepository.findById(id).orElseThrow(LocalChatNotFoundException::new);
        chatService.joinChatRoom(localChat.getChatRoom().getId(),joinId);
        return "채팅방 입장에 성공했습니다.";
    }


}

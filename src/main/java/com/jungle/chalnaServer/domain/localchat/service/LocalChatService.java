package com.jungle.chalnaServer.domain.localchat.service;

import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.localchat.domain.dto.LocalChatRequest;
import com.jungle.chalnaServer.domain.localchat.domain.dto.LocalChatResponse;
import com.jungle.chalnaServer.domain.localchat.domain.entity.LocalChat;
import com.jungle.chalnaServer.domain.localchat.repository.LocalChatRepository;
import com.jungle.chalnaServer.global.util.GeoHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocalChatService {
    public static final String REDIS_KEY = "LOCAL_CHAT";

    private final LocalChatRepository localChatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final GeoHashService geoHashService;

    public List<LocalChatResponse> findNearLocalChat(LocalChatRequest.RADIUS dto) {
        List<Long> ids = geoHashService.radius(REDIS_KEY, new Point(dto.longitude(), dto.latitude()), dto.distance()).stream()
                .map(g -> Long.valueOf(g.name()))
                .toList();
        return localChatRepository.findAllById(ids).stream()
                .map(LocalChatResponse::of)
                .toList();
    }

    public LocalChatResponse makeLocalChat(LocalChatRequest.ADD dto,final long ownerId) {
        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(ChatRoom.ChatRoomType.MATCH, 2));
        LocalChat localChat = localChatRepository.save(new LocalChat(ownerId,dto.name(), dto.description(),chatRoom, dto.latitude(), dto.longitude()));
        geoHashService.set(REDIS_KEY, new Point(dto.longitude(), dto.latitude()), String.valueOf(localChat.getId()));
        return LocalChatResponse.of(localChat);
    }







}

package com.jungle.chalnaServer.domain.localchat.service;

import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomMemberRepository;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.localchat.domain.dto.LocalChatRequest;
import com.jungle.chalnaServer.domain.localchat.domain.dto.LocalChatResponse;
import com.jungle.chalnaServer.domain.localchat.domain.entity.LocalChat;
import com.jungle.chalnaServer.domain.localchat.exception.LocalChatNotFoundException;
import com.jungle.chalnaServer.domain.localchat.exception.LocalChatNotOwnerException;
import com.jungle.chalnaServer.domain.localchat.exception.LocalChatTooCloseException;
import com.jungle.chalnaServer.domain.localchat.repository.LocalChatRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.global.util.GeoHashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LocalChatService {
    public static final String REDIS_KEY = "LOCAL_CHAT";

    private final LocalChatRepository localChatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
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
        double latitude = BigDecimal.valueOf(dto.latitude()).setScale(4, RoundingMode.DOWN).doubleValue();
        double longitude = BigDecimal.valueOf(dto.longitude()).setScale(4, RoundingMode.DOWN).doubleValue();

        if(!geoHashService.radius(REDIS_KEY, new Point(longitude, latitude), 100.0).isEmpty())
            throw new LocalChatTooCloseException();

        Member member = memberRepository.findById(ownerId).orElse(null);
        ChatRoomMember chatRoomMember = new ChatRoomMember(member, chatRoom);
        chatRoomMemberRepository.save(chatRoomMember);


        LocalChat localChat = localChatRepository.save(new LocalChat(ownerId,dto.name(), dto.description(),chatRoom, latitude, longitude));
        geoHashService.set(REDIS_KEY, new Point(dto.longitude(), dto.latitude()), String.valueOf(localChat.getId()));
        return LocalChatResponse.of(localChat);
    }

    public String removeLocalChat(final Long id, final long ownerId) {
        LocalChat localChat = localChatRepository.findById(id).orElseThrow(LocalChatNotFoundException::new);
        if(localChat.getOwnerId() != ownerId)
            throw new LocalChatNotOwnerException();
        localChat.getChatRoom().updateType(ChatRoom.ChatRoomType.CLOSED);
        localChatRepository.delete(localChat);

        geoHashService.delete(REDIS_KEY,String.valueOf(id));
        return "장소 채팅 삭제 완료.";
    }

    public String joinLocalChat(final Long id,final long joinId){
        LocalChat localChat = localChatRepository.findById(id).orElseThrow(LocalChatNotFoundException::new);
        Set<ChatRoomMember> chatRoomMembers = localChat.getChatRoom().getMembers();
        for(ChatRoomMember chatRoomMember : chatRoomMembers){
            if(chatRoomMember.getMember().getId() == joinId)
                return "이미 입장한 채팅방입니다.";
        }

        if(localChat.getChatRoom().getMemberCount() > chatRoomMembers.size()){
            Member member = memberRepository.findById(joinId).orElseThrow(MemberNotFoundException::new);
            ChatRoomMember chatRoomMember = new ChatRoomMember(member, localChat.getChatRoom());
            chatRoomMemberRepository.save(chatRoomMember);
            return "채팅방 입장에 성공했습니다.";
        }
        return "채팅방이 꽉 찼습니다.";
    }







}

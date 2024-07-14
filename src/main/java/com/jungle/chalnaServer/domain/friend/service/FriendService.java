package com.jungle.chalnaServer.domain.friend.service;

import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chat.exception.ChatRoomMemberNotFoundException;
import com.jungle.chalnaServer.domain.chat.repository.ChatRoomMemberRepository;
import com.jungle.chalnaServer.domain.chat.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.chat.service.ChatService;
import com.jungle.chalnaServer.domain.friend.domain.dto.FriendReponse;
import com.jungle.chalnaServer.domain.friend.domain.dto.FriendRequest;
import com.jungle.chalnaServer.domain.friend.domain.entity.Request;
import com.jungle.chalnaServer.domain.friend.exception.NotFriendException;
import com.jungle.chalnaServer.domain.friend.exception.RequestNotFoundException;
import com.jungle.chalnaServer.domain.friend.repository.RequestRepository;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberResponse;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.domain.relation.domain.entity.FriendStatus;
import com.jungle.chalnaServer.domain.relation.domain.entity.QRelation;
import com.jungle.chalnaServer.domain.relation.domain.entity.Relation;
import com.jungle.chalnaServer.domain.relation.domain.entity.RelationPK;
import com.jungle.chalnaServer.domain.relation.repository.RelationRepository;
import com.jungle.chalnaServer.domain.relation.service.RelationService;
import com.jungle.chalnaServer.global.exception.CustomException;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final MemberRepository memberRepository;
    private final RelationRepository relationRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final RequestRepository requestRepository;


    private final RelationService relationService;
    private final ChatService chatService;

    public List<FriendReponse.REQUEST> getSendRequest(Long senderId){
        return toResponseList(requestRepository.findAllBySenderId(senderId));
    }

    public List<FriendReponse.REQUEST> getReceiveRequest(Long receiverId){
        return toResponseList(requestRepository.findAllByReceiverId(receiverId));
    }

    private List<FriendReponse.REQUEST> toResponseList(List<Request> requests){
        return requests.stream()
                .map(FriendReponse.REQUEST::of)
                .toList();
    }

    @Transactional
    public String friendRequest(Long senderId, FriendRequest.REQUEST dto) {
        RelationPK pk = new RelationPK(senderId, dto.otherId());
        if(isFriend(pk))
            throw new CustomException("이미 친구 상태입니다.");
        Request request = requestRepository.findBySenderIdAndReceiverId(senderId, dto.otherId()).orElse(null);
        // 이미 요청이 있음
        if(request != null)
            throw new CustomException("이미 요청한 상태입니다.");

        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberIdAndChatRoomId(senderId, dto.chatRoomId()).orElseThrow(ChatRoomMemberNotFoundException::new);
        request = new Request(senderId, dto.otherId(), dto.chatRoomId(), chatRoomMember.getUserName());
        requestRepository.save(request);
        return "친구 요청이 완료되었습니다.";
    }

    @Transactional
    public String friendRequestAccept(Long memberId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(RequestNotFoundException::new);
        if (!request.getReceiverId().equals(memberId))
            throw new CustomException("올바른 요청이 아닙니다.");

        RelationPK pk = new RelationPK(request.getSenderId(), memberId);
        Relation relation = relationService.findRelation(pk);
        Relation reverse = relationService.findRelation(pk.reverse());

        relation.updateFriendStatus(FriendStatus.ACCEPTED);
        reverse.updateFriendStatus(FriendStatus.ACCEPTED);

        ChatRoom chatRoom = relation.getChatRoom();
        if (chatRoom == null) {
            Long chatRoomId = chatService.makeChatRoom(ChatRoom.ChatRoomType.FRIEND, List.of(pk.getId(), pk.getOtherId()));
            chatRoom = chatRoomRepository.findById(chatRoomId).get();
            relation.updateChatRoom(chatRoom);
            reverse.updateChatRoom(chatRoom);
        } else {
            chatService.updateChatRoomType(chatRoom.getId(), ChatRoom.ChatRoomType.FRIEND);
        }
        requestRepository.delete(request);

        return "친구 요청 수락이 성공했습니다.";
    }

    public String friendRequestReject(Long memberId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(RequestNotFoundException::new);
        if (!request.getReceiverId().equals(memberId))
            throw new CustomException("올바른 요청이 아닙니다.");
        requestRepository.delete(request);
        return "친구 요청 거절이 성공했습니다.";
    }

    public List<MemberResponse.INFO> findFriends(Long id) {
        return getMemberList(getFriendIdList(id));
    }

    @Transactional
    public FriendReponse.DETAIL getFriend(Long id, Long otherId) {
        RelationPK pk = new RelationPK(id, otherId);
        if(!isFriend(pk))
            throw new NotFriendException();
        makeChatRoom(pk);
        Relation relation = relationService.findRelation(pk);
        Member otherMember = memberRepository.findById(otherId).orElseThrow(MemberNotFoundException::new);
        return new FriendReponse.DETAIL(otherId, otherMember.getUsername(), otherMember.getMessage(),
                otherMember.getProfileImageId(), relation.getChatRoom().getId());

    }

    @Transactional
    public void makeChatRoom(RelationPK pk) {
        Relation relation = relationService.findRelation(pk);

        ChatRoom chatRoom = relation.getChatRoom();
        log.info("chatRoom: {}",chatRoom);
        if (chatRoom == null) {
            Long chatRoomId = chatService.makeChatRoom(ChatRoom.ChatRoomType.FRIEND, List.of(pk.getId(), pk.getOtherId()));
            chatRoom = chatRoomRepository.findById(chatRoomId).get();
            relation.updateChatRoom(chatRoom);
        }
    }

    private boolean isFriend(RelationPK pk) {
        Relation relation = relationService.findRelation(pk);
        return (relation.getFriendStatus().equals(FriendStatus.ACCEPTED)
                && !relation.isBlocked());
    }

    private List<Long> getFriendIdList(Long id) {
        QRelation relation = QRelation.relation;
        BooleanExpression expression = relation.relationPK.id.eq(id)
                .and(relation.friendStatus.eq(FriendStatus.ACCEPTED))
                .and(relation.isBlocked.eq(false));

        List<Long> friendIds = new ArrayList<>();
        relationRepository.findAll(expression).forEach((r -> friendIds.add(r.getRelationPK().getOtherId())));
        return friendIds;
    }

    private List<MemberResponse.INFO> getMemberList(List<Long> ids) {
        return memberRepository.findAllById(ids).stream().map(MemberResponse.INFO::of).collect(Collectors.toList());
    }

}

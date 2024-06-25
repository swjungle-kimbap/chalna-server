package com.jungle.chalnaServer.domain.friend.service;

import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomMemberRepository;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.friend.domain.dto.FriendReponse;
import com.jungle.chalnaServer.domain.friend.exception.NotFriendException;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberInfo;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.domain.relation.domain.entity.FriendStatus;
import com.jungle.chalnaServer.domain.relation.domain.entity.QRelation;
import com.jungle.chalnaServer.domain.relation.domain.entity.Relation;
import com.jungle.chalnaServer.domain.relation.domain.entity.RelationPK;
import com.jungle.chalnaServer.domain.relation.repository.RelationRepository;
import com.jungle.chalnaServer.domain.relation.service.RelationService;
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
    private final RelationService relationService;


    public List<MemberInfo> findFriends(Long id) {
        return getMemberList(getFriendIdList(id));
    }

    @Transactional
    public FriendReponse.DETAIL getFriend(Long id, Long otherId) {
        RelationPK pk = new RelationPK(id, otherId);
        checkFriend(pk);
        makeChatRoom(pk);
        Relation relation = relationService.findRelation(pk);
        Member otherMember = memberRepository.findById(otherId).orElseThrow(MemberNotFoundException::new);
        return new FriendReponse.DETAIL(otherId, otherMember.getUsername(), otherMember.getMessage(),
                otherMember.getProfileImageUrl(), relation.getChatRoom().getId());

    }

    @Transactional
    public void makeChatRoom(RelationPK pk) {
        Relation relation = relationService.findRelation(pk);

        ChatRoom chatRoom = relation.getChatRoom();
        log.info("chatRoom: {}",chatRoom);
        if (chatRoom == null) {
            Member member = memberRepository.findById(pk.getId()).orElseThrow(MemberNotFoundException::new);
            Member otherMember = memberRepository.findById(pk.getOtherId()).orElseThrow(MemberNotFoundException::new);
            chatRoom = new ChatRoom(ChatRoom.ChatRoomType.FRIEND, 2);
            chatRoomMemberRepository.save(new ChatRoomMember(member, chatRoom));
            chatRoomMemberRepository.save(new ChatRoomMember(otherMember, chatRoom));
            relation.updateChatRoom(chatRoom);
            chatRoomRepository.save(chatRoom);
        }
    }

    private void checkFriend(RelationPK pk) {
        Relation relation = relationService.findRelation(pk);
        Relation reverse = relationService.findRelation(pk.reverse());
        if (relation.getFriendStatus() != FriendStatus.ACCEPTED
                || reverse.getFriendStatus() != FriendStatus.ACCEPTED
                || relation.isBlocked()) {
            throw new NotFriendException();
        }
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

    private List<MemberInfo> getMemberList(List<Long> ids) {
        return memberRepository.findAllById(ids).stream().map(MemberInfo::of).collect(Collectors.toList());
    }

}

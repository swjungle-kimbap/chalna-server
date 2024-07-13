package com.jungle.chalnaServer.domain.relation.service;

import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chat.exception.ChatRoomNotFoundException;
import com.jungle.chalnaServer.domain.chat.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.encounter.domain.dto.EncounterRequest;
import com.jungle.chalnaServer.domain.encounter.domain.entity.Encounter;
import com.jungle.chalnaServer.domain.encounter.repository.EncounterRepository;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.domain.relation.domain.dto.RelationRequest;
import com.jungle.chalnaServer.domain.relation.domain.dto.RelationResponse;
import com.jungle.chalnaServer.domain.relation.domain.entity.FriendStatus;
import com.jungle.chalnaServer.domain.relation.domain.entity.Relation;
import com.jungle.chalnaServer.domain.relation.domain.entity.RelationPK;
import com.jungle.chalnaServer.domain.relation.exception.RelationIdInvalidException;
import com.jungle.chalnaServer.domain.relation.repository.RelationRepository;
import com.jungle.chalnaServer.global.common.repository.DeviceInfoRepository;
import com.jungle.chalnaServer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RelationService {
    private final RelationRepository relationRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final DeviceInfoRepository deviceInfoRepository;
    private final EncounterRepository encounterRepository;

    public RelationResponse findByOtherId(final Long id, final Long otherId) {
        return RelationResponse.of(findRelation(new RelationPK(id, otherId)));
    }

    public RelationResponse findAndIncreaseOverlap(final Long id, final String deviceId, EncounterRequest.LOCATION dto) {
        Long otherId = deviceInfoRepository.findById(deviceId);
        if(otherId == null){
            throw new MemberNotFoundException();
        }
        RelationPK pk = new RelationPK(id,otherId);

        Relation relation = findRelation(pk);
        Relation reverse = findRelation(pk.reverse());
        boolean increaseOverlapCount = relation.increaseOverlapCount();
        reverse.increaseOverlapCount();

        if (increaseOverlapCount) {
            // Loaction 생성
            Encounter location = Encounter.builder()
                    .memberId(id)
                    .otherId(otherId)
                    .latitude(dto.latitude())
                    .longitude(dto.longitude())
                    .build();

            encounterRepository.save(location);
        }

        return RelationResponse.of(relation);
    }

    public String friendUnblock(final Long id,final Long otherId){
        RelationPK pk = new RelationPK(id, otherId);
        Relation relation = findRelation(pk);

        relation.updateIsBlocked(false);
        return "요청에 성공했습니다.";
    }
    public String friendBlock(final Long id,final Long otherId){
        RelationPK pk = new RelationPK(id, otherId);
        Relation relation = findRelation(pk);

        relation.updateFriendStatus(FriendStatus.NOTHING);
        relation.updateIsBlocked(true);
        return "요청에 성공했습니다.";
    }

    public String friendRemove(final Long id,final Long otherId){
        RelationPK pk = new RelationPK(id, otherId);
        Relation relation = findRelation(pk);

        relation.updateFriendStatus(FriendStatus.NOTHING);
        return "요청에 성공했습니다.";
    }

    @Transactional
    public String friendAccept(final Long id, final Long otherId, RelationRequest.ACCEPT dto) {
        ChatRoom chatRoom = chatRoomRepository.findById(dto.chatRoomId()).orElseThrow(ChatRoomNotFoundException::new);

            RelationPK pk = new RelationPK(id, otherId);

        Relation relation = findRelation(pk);
            Relation reverse = findRelation(pk.reverse());

            if (relation.getFriendStatus() == FriendStatus.PENDING && reverse.getFriendStatus() != FriendStatus.ACCEPTED) {
                relation.updateFriendStatus(FriendStatus.ACCEPTED);
                reverse.updateFriendStatus(FriendStatus.ACCEPTED);
                // 채팅방 상태 변경
                chatRoom.updateType( ChatRoom.ChatRoomType.FRIEND);
                Set<ChatRoomMember> members = chatRoom.getMembers();
                for (ChatRoomMember member : members) {
                    member.updateChatRoomType(ChatRoom.ChatRoomType.FRIEND);
                }
                relation.updateChatRoom(chatRoom);
                reverse.updateChatRoom(chatRoom);
                return "요청에 성공했습니다.";
            }
            else {
                throw new CustomException("이미 친구거나, 요청하지 않은 상대입니다.");
            }

    }

    public String friendReject(final Long id, final Long otherId) {
        RelationPK pk = new RelationPK(id, otherId);
        Relation relation = findRelation(pk);
        Relation reverse = findRelation(pk.reverse());


        if (relation.getFriendStatus() == FriendStatus.PENDING && reverse.getFriendStatus() != FriendStatus.ACCEPTED) {
            reverse.updateFriendStatus(FriendStatus.NOTHING);
            return "요청에 성공했습니다.";
        } else {
            throw new CustomException("이미 친구거나, 요청하지 않은 상대입니다.");
        }
    }

    public String friendRequest(final Long id, final Long otherId){

            RelationPK pk = new RelationPK(id, otherId);
            Relation relation = findRelation(pk);
            Relation reverse = findRelation(pk.reverse());

            log.info("{} {} {}", id, otherId, pk);
            if(!reverse.isBlocked() && relation.getFriendStatus() != FriendStatus.ACCEPTED && reverse.getFriendStatus() != FriendStatus.PENDING){
                reverse.updateFriendStatus(FriendStatus.PENDING);
                return "요청에 성공했습니다.";
            }
            else{
                throw new CustomException("이미 친구거나, 요청한 상태입니다.");
            }
    }


    public Relation findRelation(RelationPK pk) {
        if (pk.getId().equals(pk.getOtherId()))
            throw new RelationIdInvalidException();
        if (!memberRepository.existsById(pk.getOtherId()))
            throw new MemberNotFoundException();
        Optional<Relation> findRelation = relationRepository.findById(pk);
        return findRelation.orElseGet(() -> createRelation(pk));
    }

    private Relation createRelation(RelationPK pk) {
        relationRepository.save(new Relation(pk.reverse()));
        return relationRepository.save(new Relation(pk));
    }


}

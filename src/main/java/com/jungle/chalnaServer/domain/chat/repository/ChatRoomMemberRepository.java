package com.jungle.chalnaServer.domain.chat.repository;


import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoomMember;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository  extends JpaRepository<ChatRoomMember, Long> {
    List<ChatRoomMember> findByMemberId(Long memberId);
    List<ChatRoomMember> findByChatRoomId(Long chatRoomId);
    Optional<ChatRoomMember> findByMemberIdAndChatRoomId(Long memberId, Long chatRoomId);
    boolean existsByMemberIdAndChatRoomId(Long memberId, Long chatRoomId);

    @Query("SELECT crm1.chatRoom FROM ChatRoomMember crm1 JOIN ChatRoomMember crm2 ON crm1.chatRoom = crm2.chatRoom " +
            "WHERE crm1.member.id = :memberId1 AND crm2.member.id = :memberId2 AND crm1.chatRoom.type <> 'LOCAL'")
    Optional<ChatRoom> findChatRoomIdByMembers(@Param("memberId1") Long memberId1, @Param("memberId2") Long memberId2);}

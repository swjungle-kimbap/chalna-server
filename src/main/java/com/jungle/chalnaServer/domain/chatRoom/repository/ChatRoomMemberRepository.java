package com.jungle.chalnaServer.domain.chatRoom.repository;


import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository  extends JpaRepository<ChatRoomMember, Long> {
    List<ChatRoomMember> findByMemberId(Long memberId);
    List<ChatRoomMember> findByChatRoomId(Long chatRoomId);
    List<ChatRoomMember> findByChatRoomIdAndIsRemovedFalse(Long chatRoomId);
    Optional<ChatRoomMember> findByMemberIdAndChatRoomId(Long memberId, Long chatRoomId);
}

package com.jungle.chalnaServer.domain.chatRoom.repository;


import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomMemberRepository  extends JpaRepository<ChatRoomMember, Long> {
    List<ChatRoomMember> findByMemberId(Long memberId);
}

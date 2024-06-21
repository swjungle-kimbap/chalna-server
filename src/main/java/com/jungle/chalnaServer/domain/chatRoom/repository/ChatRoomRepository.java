package com.jungle.chalnaServer.domain.chatRoom.repository;

import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}

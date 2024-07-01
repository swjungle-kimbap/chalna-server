package com.jungle.chalnaServer.domain.chatRoom.repository;

import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findById(Long chatRoomId);
}

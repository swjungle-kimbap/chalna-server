package com.jungle.chalnaServer.domain.localchat.repository;

import com.jungle.chalnaServer.domain.localchat.domain.entity.LocalChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalChatRepository extends JpaRepository<LocalChat,Long> {
}

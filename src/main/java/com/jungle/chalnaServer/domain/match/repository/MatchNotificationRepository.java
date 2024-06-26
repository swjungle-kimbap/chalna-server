package com.jungle.chalnaServer.domain.match.repository;

import com.jungle.chalnaServer.domain.match.domain.entity.MatchNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface MatchNotificationRepository extends JpaRepository<MatchNotification, Long> {
    //todo: 기본 메서드 외에도 추가로 필요한 조회 메서드가 있을지
    List<MatchNotification> findByReceiverId(@Param("receiverId") Long receiverId);

    List<MatchNotification> findByReceiverIdAndSenderIdAndDeleteAtAfter(Long receiverId, Long senderId, LocalDateTime deleteAt);
}

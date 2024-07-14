package com.jungle.chalnaServer.domain.friend.repository;

import com.jungle.chalnaServer.domain.friend.domain.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request,Long> {
    Optional<Request> findBySenderIdAndReceiverId(Long memberId, Long otherId);
    List<Request> findAllByReceiverId(Long receiverId);
    List<Request> findAllBySenderId(Long senderId);
}

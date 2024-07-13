package com.jungle.chalnaServer.domain.friend.repository;

import com.jungle.chalnaServer.domain.friend.domain.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request,Long> {
    Optional<Request> findByMemberIdAndOtherId(Long memberId, Long otherId);
}

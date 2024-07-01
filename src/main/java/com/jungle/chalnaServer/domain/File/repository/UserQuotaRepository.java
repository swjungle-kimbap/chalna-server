package com.jungle.chalnaServer.domain.File.repository;

import com.jungle.chalnaServer.domain.File.domain.entity.UserQuota;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserQuotaRepository extends JpaRepository<UserQuota,Long> {
    Optional<UserQuota> findByMemberAndMonthYear(Member member, String monthYear);
}

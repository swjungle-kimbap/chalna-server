package com.jungle.chalnaServer.domain.member.repository;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByKakaoId(Integer kakaoId);
    Optional<Member> findByLoginToken(String loginToken);
}

package com.jungle.chalnaServer.domain.member.repository;

import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long>, QuerydslPredicateExecutor<Member> {

    Optional<Member> findById(Long id);
    Optional<Member> findByKakaoId(Long kakaoId);
    Optional<Member> findByLoginToken(String loginToken);

}

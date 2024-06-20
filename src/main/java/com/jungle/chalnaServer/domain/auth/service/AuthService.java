package com.jungle.chalnaServer.domain.auth.service;

import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.auth.domain.dto.AuthRequest;
import com.jungle.chalnaServer.domain.auth.domain.dto.AuthResponse;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.global.util.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    public AuthResponse signup(AuthRequest dto) {
        // db에서 아이디 중복 여부 확인
        String loginToken;
        Optional<Member> findMember = memberRepository.findByKakaoId(dto.getKakaoId());
        if (findMember.isPresent()) {
            loginToken = findMember.get().getLoginToken();
        } else {
            loginToken = tokenService.generateToken();
            Member member = Member.builder()
                    .username(dto.getUsername())
                    .kakaoId(dto.getKakaoId())
                    .profileImageUrl("/images/default_image.png")
                    .loginToken(loginToken)
                    .build();

            memberRepository.save(member);
        }
        return AuthResponse.of(loginToken);
    }

    public String login(AuthRequest dto) {
        // 해당 loginToken db랑 검사
        Member member = memberRepository.findByLoginToken(dto.getLoginToken())
                .orElseThrow(MemberNotFoundException::new);

        member.update(dto);
        memberRepository.save(member);

        return "로그인성공";

    }


}
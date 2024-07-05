package com.jungle.chalnaServer.domain.temp.service;

import com.jungle.chalnaServer.domain.auth.domain.dto.AuthRequest;
import com.jungle.chalnaServer.domain.auth.domain.dto.AuthResponse;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.global.util.JwtService;
import com.jungle.chalnaServer.global.util.TokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class TempService {

    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final JwtService jwtService;

    public AuthResponse tempSignup(AuthRequest.TEMPSIGNUP dto) {
        String loginToken;
        Optional<Member> findMember = memberRepository.findByKakaoId(dto.kakaoId());
        if (findMember.isPresent()) {
            loginToken = findMember.get().getLoginToken();
        }
        else {
            loginToken = tokenService.generateToken();

            Member member = Member.builder()
                    .kakaoId(dto.kakaoId())
                    .username(dto.username())
                    .loginToken(loginToken)
                    .build();

            memberRepository.save(member);

            return AuthResponse.of(loginToken);
        }

        return AuthResponse.of(loginToken);
    }
}

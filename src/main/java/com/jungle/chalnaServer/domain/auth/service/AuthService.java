package com.jungle.chalnaServer.domain.auth.service;

import com.jungle.chalnaServer.domain.auth.domain.dto.AuthRequest;
import com.jungle.chalnaServer.domain.auth.domain.dto.AuthResponse;
import com.jungle.chalnaServer.domain.auth.domain.dto.KakaoUserInfo;
import com.jungle.chalnaServer.domain.auth.domain.entity.AuthInfo;
import com.jungle.chalnaServer.domain.auth.exception.InvalidKakaoTokenException;
import com.jungle.chalnaServer.domain.auth.repository.AuthInfoRepository;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberResponse;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.domain.settings.domain.entity.MemberSetting;
import com.jungle.chalnaServer.domain.settings.repository.MemberSettingRepository;
import com.jungle.chalnaServer.global.auth.jwt.dto.Tokens;
import com.jungle.chalnaServer.global.common.repository.DeviceInfoRepository;
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
public class AuthService {
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final KakaoTokenService kakaoTokenService;

    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final AuthInfoRepository authInfoRepository;
    private final DeviceInfoRepository deviceInfoRepository;


    public AuthResponse signup(AuthRequest.SIGNUP dto) {
        // db에서 아이디 중복 여부 확인
        String loginToken;
        Optional<Member> findMember = memberRepository.findByKakaoId(dto.kakaoId());
        if (findMember.isPresent()) {
            loginToken = findMember.get().getLoginToken();
        } else {
            // kakao access token 검증
            boolean isValid = kakaoTokenService.verifyToken(dto.accessToken());
            log.info("kakao 엑세스 토큰 = {}",isValid);
            if (!isValid ) {
                // 유효성 검증 실패 시 토큰 갱신
                String newAccessToken = kakaoTokenService.refreshToken(dto.refreshToken());

                if (newAccessToken == null || !kakaoTokenService.verifyToken(newAccessToken)) {
                    log.info("new = {}",newAccessToken);
                    throw new InvalidKakaoTokenException();
                } else {
                    dto = new AuthRequest.SIGNUP(dto.kakaoId(), dto.username(), newAccessToken, dto.refreshToken());
                }
            }
            // 검증이 되면 kakaoid, username db에 넣기
            // kakao user 정보 가져오기
            KakaoUserInfo kakaoInfo = kakaoTokenService.getUserInfo(dto.accessToken());

            String nickname = kakaoInfo.getProperties().getNickname();
            Long kakaoId = kakaoInfo.getKakaoId();

            loginToken = tokenService.generateToken();
            Member member= Member.builder()
                    .profileImageUrl("/images/default_image.png")
                    .loginToken(loginToken)
                    .username(nickname)
                    .kakaoId(kakaoId)
                    .build();
            log.info("member={}",member);

            MemberSetting memberSetting = MemberSetting.builder()
                    .id(member.getId())
                    .isAlarm(true)
                    .isFriendAlarm(false)
                    .isChatAlarm(true)
                    .isTagAlarm(false)
                    .alarmSound(true)
                    .alarmVibration(true)
                    .isDisturb(false)
                    .build();

            memberRepository.save(member);
            memberSettingRepository.save(memberSetting);

        }
        return AuthResponse.of(loginToken);
    }

    @Transactional
    public Tokens login(AuthRequest.LOGIN dto) {
        // 해당 loginToken db랑 검사
        Member member = memberRepository.findByLoginToken(dto.loginToken())
                .orElseThrow(MemberNotFoundException::new);

        String accessToken = jwtService.createAccessToken(member.getId(),dto.deviceId());
        String refreshToken = jwtService.createRefreshToken(member.getId(),dto.deviceId());

        // 인증 정보 저장
        AuthInfo authInfo = new AuthInfo(member.getId(),dto.deviceId(), dto.fcmToken(), refreshToken);
        authInfoRepository.save(authInfo);

        // 기기 정보 저장
        member.updateDeviceId(dto.deviceId());
        deviceInfoRepository.save(dto.deviceId(),member.getId());

        return new Tokens(accessToken, refreshToken);
    }

    public MemberResponse getMemberInfo(AuthRequest.LOGIN dto) {
        Member member = memberRepository.findByLoginToken(dto.loginToken())
                .orElseThrow(MemberNotFoundException::new);
        return MemberResponse.of(member);
    }


    public void logout(final Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        authInfoRepository.delete(member.getId());
        deviceInfoRepository.delete(member.getDeviceId());
    }




}
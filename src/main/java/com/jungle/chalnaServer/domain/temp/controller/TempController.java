package com.jungle.chalnaServer.domain.temp.controller;

import com.jungle.chalnaServer.domain.auth.domain.dto.AuthRequest;
import com.jungle.chalnaServer.domain.auth.domain.dto.AuthResponse;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberInfo;
import com.jungle.chalnaServer.domain.temp.service.TempService;
import com.jungle.chalnaServer.global.auth.jwt.dto.Tokens;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.global.util.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v2")
public class TempController {

    private final TempService tempService;

    @PostMapping("/login")
    public CommonResponse<?> tempLogin(@RequestBody AuthRequest.LOGIN dto, HttpServletResponse response) {

        Tokens tokens = tempService.tempLogin(dto);

        response.setHeader(JwtService.AUTHORIZATION_HEADER,tokens.accessToken());
        response.setHeader(JwtService.REFRESH_HEADER,tokens.refreshToken());

        return CommonResponse.ok("임시 로그인 성공");
    }

    @PostMapping("/signup")
    public CommonResponse<AuthResponse> tempSignup(@RequestBody AuthRequest.TEMPSIGNUP dto) {
        log.info("회원가입 ");
        AuthResponse response = tempService.tempSignup(dto);
        log.info("임시 회원={}",response);
        return CommonResponse.from(HttpStatus.OK,response);

    }
}

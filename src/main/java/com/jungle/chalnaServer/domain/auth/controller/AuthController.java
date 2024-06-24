package com.jungle.chalnaServer.domain.auth.controller;

import com.jungle.chalnaServer.domain.auth.domain.dto.AuthRequest;
import com.jungle.chalnaServer.domain.auth.service.AuthService;
import com.jungle.chalnaServer.domain.auth.domain.dto.AuthResponse;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberInfo;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.auth.jwt.dto.Tokens;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.global.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService memberService;

    private final JwtService jwtService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<AuthResponse> signup(@RequestBody AuthRequest.SIGNUP dto) {
        AuthResponse response = memberService.signup(dto);
        return CommonResponse.from(HttpStatus.CREATED,response);
}

    @PostMapping("/login")
    public CommonResponse<?> login(@RequestBody AuthRequest.LOGIN dto, HttpServletResponse response) {

        Tokens tokens = memberService.login(dto);
        MemberInfo responses = memberService.getMemberInfo(dto);

        response.setHeader(JwtService.AUTHORIZATION_HEADER,tokens.accessToken());
        response.setHeader(JwtService.REFRESH_HEADER,tokens.refreshToken());

        return CommonResponse.from(HttpStatus.OK,responses);
    }

    @PostMapping("/logout")
    public CommonResponse<?> logout(@AuthUserId final Long id) {
        memberService.logout(id);
        return CommonResponse.ok("로그아웃 성공");
    }

    @GetMapping("/test")
    public String tokenTest(HttpServletRequest request){
        return jwtService.getId(jwtService.resolveToken(request,JwtService.AUTHORIZATION_HEADER)).toString();
    }
}
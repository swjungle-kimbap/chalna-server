package com.jungle.chalnaServer.domain.member.controller;

import com.jungle.chalnaServer.domain.member.domain.dto.AuthRequest;
import com.jungle.chalnaServer.domain.member.domain.dto.AuthResponse;
import com.jungle.chalnaServer.domain.member.service.MemberService;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<?>> signup(@RequestBody AuthRequest dto) {
        AuthResponse response = memberService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.from(HttpStatus.CREATED,response,"created"));
}

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<?>> login(@RequestBody AuthRequest dto) {
        String response = memberService.login(dto);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.ok(response,"로그인 성공"));
    }
}
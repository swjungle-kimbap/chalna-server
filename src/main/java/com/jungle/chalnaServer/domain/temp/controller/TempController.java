package com.jungle.chalnaServer.domain.temp.controller;

import com.jungle.chalnaServer.domain.auth.domain.dto.AuthRequest;
import com.jungle.chalnaServer.domain.auth.domain.dto.AuthResponse;
import com.jungle.chalnaServer.domain.temp.service.TempService;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v2")
public class TempController {

    private final TempService tempService;

    @PostMapping("/signup")
    public CommonResponse<AuthResponse> tempSignup(@RequestBody AuthRequest.TEMPSIGNUP dto) {
        log.info("회원가입 ");
        AuthResponse response = tempService.tempSignup(dto);
        log.info("임시 회원={}",response);
        return CommonResponse.from(HttpStatus.OK,response);

    }


    @GetMapping("/real3")
    public String deployTest() {
        return "자동 배포 진짜 성공";
    }
}

package com.jungle.chalnaServer.domain.member.controller;

import com.jungle.chalnaServer.domain.member.domain.dto.MemberResponse;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberRequest;
import com.jungle.chalnaServer.domain.member.service.MemberService;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.global.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;

    @PatchMapping
    public CommonResponse<MemberResponse> updateMemberInfo(HttpServletRequest request,
                                                           @RequestParam(value = "username", required = false) String username,
                                                           @RequestParam(value = "message" , required = false) String message,
                                                           @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
        MemberRequest memberDto = MemberRequest.builder()
                .username(username)
                .message(message)
                .image(image)
                .build();

        return CommonResponse.ok(memberService.updateMemberInfo(id, memberDto, image));
    }

    @GetMapping
    public CommonResponse<MemberResponse> getMemberInfo(HttpServletRequest request) {

        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
        return CommonResponse.ok(memberService.getMemberInfo(id));
    }

}

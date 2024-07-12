package com.jungle.chalnaServer.domain.member.controller;

import com.jungle.chalnaServer.domain.member.domain.dto.MemberRequest;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberResponse;
import com.jungle.chalnaServer.domain.member.service.MemberService;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.global.util.JwtService;
import com.jungle.chalnaServer.infra.file.domain.dto.FileRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;

    @PatchMapping("/profile")
    public CommonResponse<MemberResponse.INFO> updateMemberProfile(@AuthUserId final Long id,
                                                           @RequestBody MemberRequest.PROFILE memberDto) {


        return CommonResponse.ok(memberService.updateMemberProfile(id, memberDto));
    }

//    @PatchMapping("/profileImage")
//    public CommonResponse<MemberResponse.PROFILE_IMAGE_UPLOAD> updateMemberInfo(@AuthUserId final Long id
//            , @RequestBody FileRequest.UPLOAD dto) {
//
//        return CommonResponse.ok(memberService.updateMemberInfo(id,dto));
//    }

    @GetMapping
    public CommonResponse<MemberResponse.INFO> getMemberInfo(@AuthUserId final Long id) {

        return CommonResponse.ok(memberService.getMemberInfo(id));
    }

}

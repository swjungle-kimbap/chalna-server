package com.jungle.chalnaServer.domain.member.controller;

import com.jungle.chalnaServer.domain.member.domain.dto.MemberRequest;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberResponse;
import com.jungle.chalnaServer.domain.member.service.MemberService;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;


    @PatchMapping("/{kakaoId}")
    public ResponseEntity<CommonResponse<?>> updateMemberInfo(@PathVariable("kakaoId") final Integer kakaoId,
                                                              @RequestParam(value = "username", required = false) String username,
                                                              @RequestParam(value = "message" , required = false) String message,
                                                              @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        MemberRequest memberDto = MemberRequest.builder()
                .username(username)
                .message(message)
                .image(image)
                .build();

        MemberResponse memberResponse = memberService.updateMemberInfo(kakaoId, memberDto, image);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.from(HttpStatus.OK,memberResponse,"Ok"));
    }

}

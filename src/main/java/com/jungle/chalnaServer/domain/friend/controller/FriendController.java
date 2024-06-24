package com.jungle.chalnaServer.domain.friend.controller;

import com.jungle.chalnaServer.domain.friend.service.FriendService;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberInfo;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend")
public class FriendController {
    private final FriendService friendService;

    @GetMapping
    public CommonResponse<List<MemberInfo>> getFriends(@AuthUserId final Long id){
        return CommonResponse.ok(friendService.findFriends(id));
    }


}

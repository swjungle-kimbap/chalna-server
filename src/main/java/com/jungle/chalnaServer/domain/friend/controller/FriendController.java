package com.jungle.chalnaServer.domain.friend.controller;

import com.jungle.chalnaServer.domain.friend.domain.dto.FriendReponse;
import com.jungle.chalnaServer.domain.friend.service.FriendService;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberResponse;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend")
public class FriendController {
    private final FriendService friendService;

    @GetMapping
    public CommonResponse<List<MemberResponse.INFO>> getFriends(@AuthUserId final Long id){
        return CommonResponse.ok(friendService.findFriends(id));
    }

    @GetMapping("/{otherId}")
    public CommonResponse<FriendReponse.DETAIL> getFriend(@AuthUserId final Long id, @PathVariable final Long otherId){
        return CommonResponse.ok(friendService.getFriend(id, otherId));
    }


}

package com.jungle.chalnaServer.domain.friend.controller;

import com.jungle.chalnaServer.domain.friend.domain.dto.FriendReponse;
import com.jungle.chalnaServer.domain.friend.domain.dto.FriendRequest;
import com.jungle.chalnaServer.domain.friend.service.FriendService;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberResponse;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend")
public class FriendController {
    private final FriendService friendService;


    @PostMapping
    public CommonResponse<String> friendRequest(@AuthUserId Long userId, @RequestBody FriendRequest.REQUEST dto) {
        return CommonResponse.ok(friendService.friendRequest(userId, dto));
    }


    @GetMapping
    public CommonResponse<List<MemberResponse.INFO>> getFriends(@AuthUserId final Long id){
        return CommonResponse.ok(friendService.findFriends(id));
    }

    @GetMapping("/{otherId}")
    public CommonResponse<FriendReponse.DETAIL> getFriend(@AuthUserId final Long id, @PathVariable final Long otherId){
        return CommonResponse.ok(friendService.getFriend(id, otherId));
    }


}

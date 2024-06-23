package com.jungle.chalnaServer.domain.relation.controller;

import com.jungle.chalnaServer.domain.relation.domain.dto.RelationResponse;
import com.jungle.chalnaServer.domain.relation.service.RelationService;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/relation")
@RequiredArgsConstructor
public class RelationController {

    private final RelationService relationService;
    @GetMapping("/{otherId}")
    public CommonResponse<RelationResponse> getRelation(@AuthUserId final Long id, @PathVariable final Long otherId) {
        return CommonResponse.ok(relationService.findByOtherId(id, otherId));
    }

    @PostMapping("/{otherId}")
    public CommonResponse<RelationResponse> overLap(@AuthUserId final Long id, @PathVariable final Long otherId) {
        return CommonResponse.ok(relationService.findAndIncreaseOverlap(id, otherId));
    }

    @PatchMapping("/unblock/{otherId}")
    public CommonResponse<String> friendUnblock(@AuthUserId final Long id, @PathVariable final Long otherId){
        return CommonResponse.from("200",null,relationService.friendUnblock(id,otherId));
    }
    @PatchMapping("/block/{otherId}")
    public CommonResponse<String> friendBlock(@AuthUserId final Long id, @PathVariable final Long otherId){
        return CommonResponse.from("200",null,relationService.friendBlock(id,otherId));
    }
    @PatchMapping("/delete/{otherId}")
    public CommonResponse<String> friendDelete(@AuthUserId final Long id, @PathVariable final Long otherId){
        return CommonResponse.from("200",null,relationService.friendRemove(id,otherId));
    }
    @PatchMapping("/request/{otherId}")
    public CommonResponse<String> friendRequest(@AuthUserId final Long id, @PathVariable final Long otherId){
        return CommonResponse.from("200",null,relationService.friendRequest(id,otherId));
    }

    @PatchMapping("/accept/{otherId}")
    public CommonResponse<String> friendAccept(@AuthUserId final Long id, @PathVariable final Long otherId) {
        return CommonResponse.from("200", null, relationService.friendAccept(id, otherId));
    }


    @PatchMapping("/reject/{otherId}")
    public CommonResponse<String> friendReject(@AuthUserId final Long id, @PathVariable final Long otherId) {
        return CommonResponse.from("200", null, relationService.friendReject(id, otherId));
    }
}

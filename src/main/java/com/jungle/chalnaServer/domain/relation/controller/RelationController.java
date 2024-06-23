package com.jungle.chalnaServer.domain.relation.controller;

import com.jungle.chalnaServer.domain.relation.domain.dto.RelationResponse;
import com.jungle.chalnaServer.domain.relation.service.RelationService;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @PatchMapping("/{otherId}")
    public CommonResponse<String> friendRequest(@AuthUserId final Long id, @PathVariable final Long otherId){
        return CommonResponse.from("200",null,relationService.FriendRequest(id,otherId));
    }
}

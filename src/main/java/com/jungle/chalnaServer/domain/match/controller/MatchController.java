package com.jungle.chalnaServer.domain.match.controller;


import com.jungle.chalnaServer.domain.match.domain.dto.MatchRequest;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchResponse;
import com.jungle.chalnaServer.domain.match.service.MatchService;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.global.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MatchController {

    private final MatchService matchService;
    private final JwtService jwtService;

    @GetMapping("/match")
    public CommonResponse<List<Map<String, String>>> matchMessageList(@AuthUserId final Long id) {
        return CommonResponse.from(HttpStatus.OK, matchService.matchList(id));
    }
    @PostMapping("/match")
    public CommonResponse<MatchResponse.MESSAGE_SEND> matchMessageSend(@AuthUserId final Long id, @RequestBody MatchRequest.Send dto){
        return CommonResponse.ok(matchService.matchMessageSend(dto, id));
    }

    @PostMapping("/match/accept/{notificationId}")
    public CommonResponse<Map<String, String>> matchAccept(@PathVariable("notificationId") Long notificationId) throws Exception {
        return CommonResponse.from(HttpStatus.OK, matchService.matchAccept(notificationId));
    }

    @PutMapping("/match/reject/{notificationId}")
    public CommonResponse<Map<String, String>> matchReject(@PathVariable("notificationId") Long notificationId) {
        return CommonResponse.from(HttpStatus.OK, matchService.matchReject(notificationId));
    }

    @PutMapping("/match/reject")
    public CommonResponse<Map<String, String>> matchRejectAll(@AuthUserId Long receiverId) {
        return CommonResponse.from(HttpStatus.OK, matchService.matchAllReject(receiverId));
    }
}

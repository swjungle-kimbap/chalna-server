package com.jungle.chalnaServer.domain.match.controller;


import com.jungle.chalnaServer.domain.match.domain.dto.MatchRequest;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchResponse;
import com.jungle.chalnaServer.domain.match.service.MatchService;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.global.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static java.lang.Long.parseLong;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MatchController {

    private final MatchService matchService;
    private final JwtService jwtService;

    @GetMapping("/match")
    public CommonResponse<List<Map<String, String>>> matchMessageList(HttpServletRequest request) {
        return CommonResponse.from(HttpStatus.OK, matchService.matchList(jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER))));
    }
    @PostMapping("/match")
    public CommonResponse<Map<String, String>> matchMessageSend(@RequestBody MatchRequest.Send dto, HttpServletRequest request) throws Exception {
        //todo: requestDto header의 loginToken, receiver userId 검증
        //todo: response로 돌아오는 저장한 receiver userId 개수 requestDto receiver와 비교하여 성공 여부 검증
        String token = jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER);
        Long senderId = jwtService.getId(token);

        return CommonResponse.from(HttpStatus.CREATED, matchService.matchMessageSend(dto, senderId));
    }

    @PostMapping("/match/accept/{notificationId}")
    public CommonResponse<Map<String, String>> matchAccept(@PathVariable("notificationId") Long notificationId) {
        return CommonResponse.from(HttpStatus.OK, matchService.matchAccept(notificationId));
    }

    @PutMapping("/match/reject/{notificationId}")
    public CommonResponse<Map<String, String>> matchReject(@PathVariable("notificationId") Long notificationId) {
        return CommonResponse.from(HttpStatus.OK, matchService.matchReject(notificationId));
    }
}

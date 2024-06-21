package com.jungle.chalnaServer.domain.match.controller;


import com.jungle.chalnaServer.domain.match.domain.dto.MatchRequest;
import com.jungle.chalnaServer.domain.match.domain.dto.MatchResponse;
import com.jungle.chalnaServer.domain.match.service.MatchService;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.global.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MatchController {

    private final MatchService matchService;
    private final JwtService jwtService;

    @PostMapping("/match")
    public CommonResponse<MatchResponse> matchMessageSend(@RequestBody MatchRequest.Send dto, @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        //todo: requestDto header의 loginToken, receiver userId 검증
        //todo: response로 돌아오는 저장한 receiver userId 개수 requestDto receiver와 비교하여 성공 여부 검증
        String token = authorizationHeader.replace(JwtService.BEARER_PREFIX, "").trim();
        MatchResponse response = matchService.matchMessageSend(dto, jwtService.getId(token));
        return CommonResponse.from(HttpStatus.CREATED, response);
    }
}

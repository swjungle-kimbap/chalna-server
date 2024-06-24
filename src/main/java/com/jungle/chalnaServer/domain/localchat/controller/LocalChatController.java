package com.jungle.chalnaServer.domain.localchat.controller;

import com.jungle.chalnaServer.domain.localchat.domain.dto.LocalChatRequest;
import com.jungle.chalnaServer.domain.localchat.domain.dto.LocalChatResponse;
import com.jungle.chalnaServer.domain.localchat.service.LocalChatService;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/localchat")
@RequiredArgsConstructor
public class LocalChatController {
    private final LocalChatService localChatService;

    @GetMapping
    public CommonResponse<List<LocalChatResponse>> findLocalChat(@RequestBody LocalChatRequest.RADIUS dto){
        return CommonResponse.ok(localChatService.findNearLocalChat(dto));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public  CommonResponse<LocalChatResponse> addLocalChat(@RequestBody LocalChatRequest.ADD dto){
        return CommonResponse.from(HttpStatus.CREATED, localChatService.makeLocalChat(dto));
    }



}

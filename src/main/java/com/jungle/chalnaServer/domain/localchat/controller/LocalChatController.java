package com.jungle.chalnaServer.domain.localchat.controller;

import com.jungle.chalnaServer.domain.localchat.domain.dto.LocalChatRequest;
import com.jungle.chalnaServer.domain.localchat.domain.dto.LocalChatResponse;
import com.jungle.chalnaServer.domain.localchat.service.LocalChatService;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/localchat")
@RequiredArgsConstructor
public class LocalChatController {
    private final LocalChatService localChatService;

//    @GetMapping
//    public CommonResponse<List<LocalChatResponse>> findLocalChat(@RequestBody LocalChatRequest.RADIUS dto){
//        return CommonResponse.ok(localChatService.findNearLocalChat(dto));
//    }
    @GetMapping
    public CommonResponse<List<LocalChatResponse>> findLocalChat(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam("distance") Double distance) {

        LocalChatRequest.RADIUS dto = new LocalChatRequest.RADIUS(latitude, longitude, distance);
        return CommonResponse.ok(localChatService.findNearLocalChat(dto));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public  CommonResponse<LocalChatResponse> addLocalChat(@RequestBody LocalChatRequest.ADD dto, @AuthUserId final Long id){
        return CommonResponse.from(HttpStatus.CREATED, localChatService.makeLocalChat(dto,id));
    }

    @DeleteMapping("/{id}")
    public CommonResponse<String> deleteLocalChat(@PathVariable final Long id, @AuthUserId final Long ownerId){
        return CommonResponse.ok(localChatService.removeLocalChat(id, ownerId));
    }

    @PostMapping("/{id}")
    public CommonResponse<String> joinLocalChat(@PathVariable final Long id, @AuthUserId final Long joinId){
        return CommonResponse.ok(localChatService.joinLocalChat(id, joinId));
    }


}

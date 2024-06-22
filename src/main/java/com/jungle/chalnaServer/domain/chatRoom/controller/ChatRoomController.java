package com.jungle.chalnaServer.domain.chatRoom.controller;

import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chatRoom.domain.dto.ChatRoomRequest;
import com.jungle.chalnaServer.domain.chatRoom.domain.dto.ChatRoomResponse;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.service.ChatRoomService;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.global.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/chatRoom")
@Log4j2
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final JwtService jwtService;

    // 채팅방 목록 조회
    @GetMapping
    public CommonResponse<Map<String, Object>> getChatRoomList(HttpServletRequest request) {
        Long memberId = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));

        List<ChatRoomResponse> list = chatRoomService.getChatRoomList(memberId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("list", list);
        return CommonResponse.ok(responseData);
    }

    // 채팅 메시지 목록 조회
    @GetMapping("/message/{chatRoomId}")
    public CommonResponse<Map<String, Object>> getChatRoomMessage(@PathVariable Long chatRoomId, @RequestParam LocalDateTime lastLeaveAt, HttpServletRequest request) {
        Long memberId = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));

        List<ChatMessageResponse> list = chatRoomService.getChatMessages(memberId,chatRoomId, lastLeaveAt);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("list", list);
        return CommonResponse.ok(responseData);
    }

    // 임시 api 채팅 방 만들기
    @PostMapping
    public void makeChatRoom(@RequestBody ChatRoomRequest chatRoomRequest) {
        List<Long> memberIdList = chatRoomRequest.getMemberIdList();
        chatRoomService.makeChatRoom(ChatRoom.ChatRoomType.MATCH, 2, memberIdList);
    }


    // 채팅방 나가기
}

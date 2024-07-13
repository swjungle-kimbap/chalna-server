package com.jungle.chalnaServer.domain.chat.controller;

import com.jungle.chalnaServer.domain.chat.service.ChatService;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatRoomRequest;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatRoomResponse;
import com.jungle.chalnaServer.domain.chat.domain.entity.ChatRoom;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.global.util.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/chatroom")
@Log4j2
public class ChatRoomController {

    private final ChatService chatService;
    private final JwtService jwtService;

    // 채팅방 목록 조회
    @GetMapping
    public CommonResponse<List<ChatRoomResponse.CHATROOM>> getChatRoomList(@AuthUserId final Long id) {
        return CommonResponse.ok(chatService.getChatRoomList(id));
    }

    // 채팅 메시지 목록 조회
    @GetMapping("/message/{chatRoomId}")
    public CommonResponse<ChatRoomResponse.MESSAGES> getChatRoomMessage(@PathVariable Long chatRoomId,@AuthUserId final Long memberId, @RequestParam(defaultValue = "false") boolean includePrevious) {
        return CommonResponse.ok(chatService.getChatMessages(memberId, chatRoomId, includePrevious));
    }

    // 채팅방 나가기
    @DeleteMapping("/leave/{chatRoomId}")
    public CommonResponse<String> leaveChatRoom(@PathVariable Long chatRoomId, @AuthUserId final Long memberId) {
        chatService.leaveChatRoom(chatRoomId, memberId);
        return CommonResponse.ok("성공");
    }

    @PostMapping("/join/{chatRoomId}")
    public CommonResponse<String> joinChatRoom(@PathVariable Long chatRoomId, @AuthUserId final Long memberId) {
        chatService.joinChatRoom(chatRoomId, memberId);
        return CommonResponse.ok("성공");
    }


    // 임시 api 채팅 방 만들기
    @PostMapping
    public CommonResponse<Long> makeChatRoom(@RequestBody ChatRoomRequest chatRoomRequest) {
        List<Long> memberIdList = chatRoomRequest.getMemberIdList();
        return CommonResponse.ok(chatService.makeChatRoom(ChatRoom.ChatRoomType.MATCH,  memberIdList));
    }
}

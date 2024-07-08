package com.jungle.chalnaServer.domain.chat.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomRequest {
    private List<Long> memberIdList;

    public ChatRoomRequest(List<Long> memberIdList) {
        this.memberIdList = memberIdList;
    }
}

package com.jungle.chalnaServer.domain.chatRoom.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public  class MemberInfo {
    private Long memberId;
    private String username;


//    @JsonProperty("memberId") // JSON 필드명 지정
    public Long getMemberId() {
        return memberId;
    }

//    @JsonProperty("username") // JSON 필드명 지정
    public String getUsername() {
        return username;
    }
    public MemberInfo(Long memberId, String username) {
        this.memberId = memberId;
        this.username = username;
    }

}

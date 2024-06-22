package com.jungle.chalnaServer.domain.match.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public enum MatchNotificationStatus {
    SEND,    // 인연 메시지를 보내기만 한 상태
    ACCEPT, // 인연 메시지를 수락한 상태
    REJECT  // 인연 메시지를 거절한 상태
}
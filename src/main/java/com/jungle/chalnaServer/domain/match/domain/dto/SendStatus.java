package com.jungle.chalnaServer.domain.match.domain.dto;

public enum SendStatus {
    SUCCESS,        // 성공
    MATCHING,       // 인연 메시지를 수락해 대화 중
    SEND,           // 이미 보낸 상태
    FRIEND,         // 친구
    FAIL            // 실패
}

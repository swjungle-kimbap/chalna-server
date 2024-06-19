package com.jungle.chalnaServer.global.exception;

import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Set;

@Getter
public enum GlobalErrorCode {
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.", Set.of()),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST,"해당 회원을 찾을 수 없습니다.",Set.of(MemberNotFoundException.class));


    private final HttpStatusCode status;
    private final String code;
    private final String message;
    private final Set<Class<? extends Exception>> exceptions;

    GlobalErrorCode(HttpStatusCode status, String message, Set<Class<? extends Exception>> exceptions) {
        this.status = status;
        this.code = String.valueOf(status.value());
        this.message = message;
        this.exceptions = exceptions;
    }
}

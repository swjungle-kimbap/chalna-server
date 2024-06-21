package com.jungle.chalnaServer.global.exception;

import com.jungle.chalnaServer.domain.match.exception.NotificationNotFoundException;
import com.jungle.chalnaServer.domain.member.exception.FileStorageException;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.settings.exception.TagsNotFoundException;
import com.jungle.chalnaServer.infra.fcm.exception.FCMTokenNotFoundException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

@Getter
public enum GlobalErrorCode {
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.", Set.of()),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST,"해당 회원을 찾을 수 없습니다.",Set.of(MemberNotFoundException.class)),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST,"올바르지 않은 입력 값입니다.",Set.of(HttpMessageNotReadableException.class,MethodArgumentNotValidException.class)),
    INVALID_TOKEN(HttpStatus.FORBIDDEN,"유효하지 않은 토큰입니다.",Set.of()),
    FCM_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 회원의 토큰을 찾을 수 없습니다.", Set.of(FCMTokenNotFoundException.class)),
    FILE_STORAGE(HttpStatus.BAD_REQUEST,"저장할 디렉토리를 찾을 수 없습니다.",Set.of(FileStorageException.class)),
    TAG_NOT_FOUND(HttpStatus.BAD_REQUEST,"삭제할 태그가 존재하지 않습니다.",Set.of(TagsNotFoundException.class)),
    NOTIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST,"해당 메시지를 찾을 수 없습니다.", Set.of(NotificationNotFoundException.class));

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

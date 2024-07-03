package com.jungle.chalnaServer.global.exception;

import com.jungle.chalnaServer.domain.chatRoom.exception.NotFoundChatRoomException;
import com.jungle.chalnaServer.infra.file.exception.FailToUploadS3Exception;
import com.jungle.chalnaServer.infra.file.exception.MaxFileSizeException;
import com.jungle.chalnaServer.domain.auth.exception.InvalidKakaoTokenException;
import com.jungle.chalnaServer.domain.chatRoom.exception.ChatRoomMemberNotFoundException;
import com.jungle.chalnaServer.domain.chatRoom.exception.ChatRoomNotFoundException;
import com.jungle.chalnaServer.domain.friend.exception.NotFriendException;
import com.jungle.chalnaServer.domain.localchat.exception.LocalChatNotFoundException;
import com.jungle.chalnaServer.domain.localchat.exception.LocalChatNotOwnerException;
import com.jungle.chalnaServer.domain.localchat.exception.LocalChatTooCloseException;
import com.jungle.chalnaServer.domain.match.exception.NotificationNotFoundException;
import com.jungle.chalnaServer.domain.member.exception.FileStorageException;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.relation.exception.RelationIdInvalidException;
import com.jungle.chalnaServer.domain.settings.exception.TagsNotFoundException;
import com.jungle.chalnaServer.global.auth.jwt.exception.InvalidJwtTokenException;
import com.jungle.chalnaServer.infra.fcm.exception.FCMTokenNotFoundException;
import com.jungle.chalnaServer.infra.file.exception.FailToUploadS3Exception;
import com.jungle.chalnaServer.infra.file.exception.MaxFileSizeException;
import com.jungle.chalnaServer.infra.file.exception.NotFoundFileInfoException;
import com.jungle.chalnaServer.infra.file.exception.NotFoundS3ObjectException;
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
    INVALID_TOKEN(HttpStatus.FORBIDDEN,"유효하지 않은 토큰입니다.",Set.of(InvalidJwtTokenException.class)),
    FCM_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 회원의 토큰을 찾을 수 없습니다.", Set.of(FCMTokenNotFoundException.class)),
    FILE_STORAGE(HttpStatus.BAD_REQUEST,"저장할 디렉토리를 찾을 수 없습니다.",Set.of(FileStorageException.class)),
    NOTIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST,"해당 메시지를 찾을 수 없습니다.", Set.of(NotificationNotFoundException.class)),
    RELATION_ID_INVALID(HttpStatus.BAD_REQUEST,"유효하지 않은 관계입니다.",Set.of(RelationIdInvalidException.class)),
    TAG_NOT_FOUND(HttpStatus.BAD_REQUEST,"삭제할 태그가 존재하지 않습니다.",Set.of(TagsNotFoundException.class)),
    INVALID_KAKAO_ACCESS_TOKEN(HttpStatus.BAD_REQUEST,"카카오 액세스 토큰이 유효하지 않습니다.",Set.of(InvalidKakaoTokenException.class)),
    LOCALCHAT_TOO_CLOSE(HttpStatus.BAD_REQUEST,"주변에 가까운 장소 채팅이 있습니다.",Set.of(LocalChatTooCloseException.class)),
    LOCALCHAT_NOT_FOUND(HttpStatus.BAD_REQUEST,"해당 장소 채팅을 찾을 수 없습니다.",Set.of(LocalChatNotFoundException.class)),
    LOCALCHAT_NOT_OWNER(HttpStatus.BAD_REQUEST,"채팅방의 주인만 삭제할 수 있습니다.",Set.of(LocalChatNotOwnerException.class)),
    NOT_FRIEND(HttpStatus.BAD_REQUEST,"친구가 아닙니다.",Set.of(NotFriendException.class)),
    MAX_FILE_UPLOAD(HttpStatus.BAD_REQUEST,"전송 파일 크기는 2.5MB를 넘을 수 없습니다.", Set.of(MaxFileSizeException.class)),
    FAIL_TO_UPLOAD_S3(HttpStatus.BAD_REQUEST,"S3 업로드를 실패했습니다.",Set.of(FailToUploadS3Exception.class)),
    CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST,"채팅방을 찾을 수 없습니다.",Set.of(ChatRoomNotFoundException.class)),
    CHATROOM_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST,"채팅방을 찾을 수 없습니다.",Set.of(ChatRoomMemberNotFoundException.class)),
    NOT_FOUND_FILE_INFO(HttpStatus.BAD_REQUEST,"해당 파일을 찾을 수 없습니다.", Set.of(NotFoundFileInfoException.class)),
    NOT_FOUND_S3_OBJECT(HttpStatus.BAD_REQUEST,"해당 파일의 만료기간이 지났습니다.",Set.of(NotFoundS3ObjectException.class)),
    NOT_FOUND_CHATROOM(HttpStatus.BAD_REQUEST,"해당 채팅방이 없습니다.",Set.of(NotFoundChatRoomException.class));


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

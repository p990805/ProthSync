package com.prothsync.prothsync.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements ErrorCode {

    CANNOT_SUSPEND_ADMIN(HttpStatus.BAD_REQUEST, "관리자 계정은 정지할 수 없습니다."),
    USER_NOT_SUSPENDED(HttpStatus.BAD_REQUEST, "정지 상태가 아닌 사용자입니다."),
    USER_ALREADY_SUSPENDED(HttpStatus.CONFLICT, "이미 정지된 사용자입니다."),
    CANNOT_CHANGE_OWN_ROLE(HttpStatus.BAD_REQUEST, "자신의 권한은 변경할 수 없습니다."),
    SUSPEND_REASON_REQUIRED(HttpStatus.BAD_REQUEST, "정지 사유는 필수입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
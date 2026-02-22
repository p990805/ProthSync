package com.prothsync.prothsync.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),
    NOTIFICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "알림에 접근할 권한이 없습니다."),
    NOTIFICATION_RECIPIENT_NULL(HttpStatus.BAD_REQUEST, "알림 수신자 ID는 필수입니다."),
    NOTIFICATION_ACTOR_NULL(HttpStatus.BAD_REQUEST, "알림 발생자 ID는 필수입니다."),
    NOTIFICATION_TYPE_NULL(HttpStatus.BAD_REQUEST, "알림 타입은 필수입니다."),
    NOTIFICATION_MESSAGE_NULL(HttpStatus.BAD_REQUEST, "알림 메시지는 필수입니다.");

    private final HttpStatus status;
    private final String message;
}
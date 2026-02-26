package com.prothsync.prothsync.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {

    REPORTER_ID_NULL(HttpStatus.BAD_REQUEST, "신고자 ID가 NULL 입니다."),
    TARGET_TYPE_NULL(HttpStatus.BAD_REQUEST, "신고 대상 유형이 NULL 입니다."),
    TARGET_ID_NULL(HttpStatus.BAD_REQUEST, "신고 대상 ID가 NULL 입니다."),
    REASON_NULL(HttpStatus.BAD_REQUEST, "신고 사유가 NULL 입니다."),
    DESCRIPTION_TOO_LONG(HttpStatus.BAD_REQUEST, "신고 상세 사유는 500자를 초과할 수 없습니다."),
    CANNOT_REPORT_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 신고할 수 없습니다."),
    ALREADY_REPORTED(HttpStatus.CONFLICT, "이미 신고한 대상입니다."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "신고 정보를 찾을 수 없습니다."),
    REPORT_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "신고 대상을 찾을 수 없습니다."),
    INVALID_PROCESS_STATUS(HttpStatus.BAD_REQUEST, "처리 상태는 ACCEPTED 또는 REJECTED만 가능합니다."),
    REPORT_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 신고입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
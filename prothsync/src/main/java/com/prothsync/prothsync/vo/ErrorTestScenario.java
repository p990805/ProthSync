package com.prothsync.prothsync.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorTestScenario {
    RUNTIME("일반 런타임 오류", Level.ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
    NULL_POINTER("null 참조 오류", Level.ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
    ARITHMETIC("0으로 나누기 오류", Level.ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_CONNECTION("DB 연결 실패 (모의)", Level.ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_CONSTRAINT("DB 유니크 제약 조건 위반 (모의)", Level.ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
    REDIS_CONNECTION("Redis 연결 실패 (모의)", Level.ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_TIMEOUT("외부 API 응답 시간 초과 (모의, 실제 대기 없음)", Level.ERROR, HttpStatus.GATEWAY_TIMEOUT),
    NESTED_CAUSE("여러 단계의 원인 예외", Level.ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("잘못된 입력: WARN과 HTTP 400", Level.WARN, HttpStatus.BAD_REQUEST),
    WARN_ONLY("예외 없이 WARN 로그만 기록", Level.WARN, HttpStatus.OK),
    ERROR_LOG_ONLY("예외 없이 ERROR 로그만 기록", Level.ERROR, HttpStatus.OK);

    private final String description;
    private final Level logLevel;
    private final HttpStatus httpStatus;
}

package com.prothsync.prothsync.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SearchErrorCode implements ErrorCode {

    SEARCH_KEYWORD_EMPTY(HttpStatus.BAD_REQUEST, "검색 키워드는 필수입니다."),
    SEARCH_KEYWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "검색 키워드는 최소 1자 이상이어야 합니다."),
    SEARCH_KEYWORD_TOO_LONG(HttpStatus.BAD_REQUEST, "검색 키워드는 최대 50자까지 가능합니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
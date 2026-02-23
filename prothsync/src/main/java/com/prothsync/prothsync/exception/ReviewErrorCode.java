package com.prothsync.prothsync.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    REVIEW_CASE_REQUEST_ID_NULL(HttpStatus.BAD_REQUEST, "의뢰 ID는 필수입니다."),
    REVIEW_REVIEWER_ID_NULL(HttpStatus.BAD_REQUEST, "작성자 ID는 필수입니다."),
    REVIEW_REVIEWEE_ID_NULL(HttpStatus.BAD_REQUEST, "대상자 ID는 필수입니다."),
    REVIEW_RATING_INVALID(HttpStatus.BAD_REQUEST, "평점은 1~5 사이여야 합니다."),
    REVIEW_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "리뷰 내용이 너무 깁니다.(최대 500자)"),

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),

    REVIEW_ACCESS_DENIED(HttpStatus.FORBIDDEN, "리뷰에 접근할 권한이 없습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 의뢰에 대한 리뷰가 이미 존재합니다."),
    REVIEW_CASE_NOT_COMPLETED(HttpStatus.CONFLICT, "완료된 의뢰에만 리뷰를 작성할 수 있습니다."),
    REVIEW_NOT_CLIENT(HttpStatus.FORBIDDEN, "의뢰자만 리뷰를 작성할 수 있습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
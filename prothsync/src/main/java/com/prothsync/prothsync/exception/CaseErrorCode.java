package com.prothsync.prothsync.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CaseErrorCode implements ErrorCode {

    CASE_TITLE_IS_NULL(HttpStatus.BAD_REQUEST, "의뢰 제목은 필수입니다."),
    CASE_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "의뢰 제목이 너무 깁니다.(최대 100자)"),
    CASE_DESCRIPTION_TOO_LONG(HttpStatus.BAD_REQUEST, "의뢰 설명이 너무 깁니다.(최대 3000자)"),
    CASE_CATEGORY_IS_NULL(HttpStatus.BAD_REQUEST, "의뢰 카테고리는 필수입니다."),
    CASE_CLIENT_ID_IS_NULL(HttpStatus.BAD_REQUEST, "의뢰자 ID가 NULL입니다."),
    CASE_DEADLINE_IS_PAST(HttpStatus.BAD_REQUEST, "희망 납기일은 과거일 수 없습니다."),
    CASE_BUDGET_INVALID(HttpStatus.BAD_REQUEST, "예산은 0보다 커야 합니다."),

    CASE_NOT_FOUND(HttpStatus.NOT_FOUND, "의뢰를 찾을 수 없습니다."),
    CASE_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "의뢰요청을 찾을 수 없습니다."),
    CASE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "의뢰에 접근할 권한이 없습니다."),

    CASE_NOT_OPEN(HttpStatus.CONFLICT, "모집중인 의뢰가 아닙니다."),
    CASE_ALREADY_TERMINATED(HttpStatus.CONFLICT, "이미 종료된 의뢰입니다."),
    CASE_NO_ACCEPTED_PROPOSAL(HttpStatus.CONFLICT, "수락된 제안이 없어 진행할 수 없습니다."),

    PROPOSAL_MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, "제안 메시지가 너무 깁니다.(최대 1000자)"),
    PROPOSAL_PRICE_INVALID(HttpStatus.BAD_REQUEST, "제안 금액은 0보다 커야 합니다."),
    PROPOSAL_DAYS_INVALID(HttpStatus.BAD_REQUEST, "예상 소요일은 1일 이상이어야 합니다."),

    PROPOSAL_NOT_FOUND(HttpStatus.NOT_FOUND, "제안을 찾을 수 없습니다."),
    PROPOSAL_ACCESS_DENIED(HttpStatus.FORBIDDEN, "제안에 접근할 권한이 없습니다."),
    PROPOSAL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 의뢰에 제안을 보냈습니다."),
    PROPOSAL_OWN_CASE(HttpStatus.BAD_REQUEST, "본인의 의뢰에는 제안할 수 없습니다."),
    PROPOSAL_ALREADY_DECIDED(HttpStatus.CONFLICT, "이미 처리된 제안입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
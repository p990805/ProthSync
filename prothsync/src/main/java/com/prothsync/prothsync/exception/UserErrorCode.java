package com.prothsync.prothsync.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    USERNAME_IS_NULL(HttpStatus.BAD_REQUEST, "아이디가 비어 있습니다."),
    PASSWORD_IS_NULL(HttpStatus.BAD_REQUEST, "비밀번호가 비어 있습니다."),
    NICKNAME_IS_NULL(HttpStatus.BAD_REQUEST, "닉네임이 비어 있습니다."),
    BIRTHDAY_IS_NULL(HttpStatus.BAD_REQUEST, "생년월일이 비어 있습니다."),
    BIRTHDAY_IS_FUTURE(HttpStatus.BAD_REQUEST, "생년월일은 미래일 수 없습니다."),
    BIRTHDAY_TOO_OLD(HttpStatus.BAD_REQUEST, "생년월일이 유효하지 않습니다."),
    ADDRESS_IS_NULL(HttpStatus.BAD_REQUEST, "주소가 비어 있습니다."),
    EMAIL_IS_NULL(HttpStatus.BAD_REQUEST, "이메일이 비어 있습니다."),
    EMAIL_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
    USER_TYPE_IS_NULL(HttpStatus.BAD_REQUEST, "사용자 유형이 비어 있습니다."),
    USERNAME_TOO_LONG(HttpStatus.BAD_REQUEST, "아이디는 20자를 초과할 수 없습니다."),
    NICKNAME_TOO_LONG(HttpStatus.BAD_REQUEST, "닉네임은 10자를 초과할 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}

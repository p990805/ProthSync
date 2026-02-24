package com.prothsync.prothsync.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {

    POST_USER_ID_NULL(HttpStatus.NOT_FOUND, "사용자 ID가 NULL 입니다."),
    POST_CATEGORY_NULL(HttpStatus.NOT_FOUND, "카테고리가 NULL 입니다."),
    POST_VISIBILITY_NULL(HttpStatus.NOT_FOUND, "공개범위가 NULL 입니다."),
    POST_ID_NULL(HttpStatus.NOT_FOUND, "게시글 ID가 NULL 입니다."),
    POST_CONTENT_NULL(HttpStatus.NOT_FOUND, "댓글 내용이 NULL 입니다."),
    POST_HASHTAG_ID_NULL(HttpStatus.NOT_FOUND, "해시태그 ID가 NULL 입니다."),
    POST_HASHTAG_NULL(HttpStatus.NOT_FOUND, "해시태그 이름이 NULL 입니다."),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "게시글에 접근할 권한이 없습니다."),
    POST_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "게시글 내용이 너무 깁니다.(최대 2000자)"),
    POST_CATEGORY_REQUIRED(HttpStatus.BAD_REQUEST, "카테고리는 필수입니다."),
    POST_VISIBILITY_REQUIRED(HttpStatus.BAD_REQUEST, "공개 범위는 필수입니다."),

    IMAGE_PATH_NULL(HttpStatus.NOT_FOUND, "이미지 경로가 NULL 입니다."),
    IMAGE_ORIGINAL_NAME_NULL(HttpStatus.NOT_FOUND, "이미지 원본 이름이 NULL 입니다."),
    IMAGE_STORED_NAME_NULL(HttpStatus.NOT_FOUND, "이미지 저장 이름이 NULL 입니다."),
    IMAGE_DISPLAY_ORDER_INVALID(HttpStatus.NOT_FOUND, "이미지 순서는 0 이상이어야 합니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제에 실패했습니다."),
    IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "게시글당 이미지는 최대 10개까지 등록 가능합니다."),
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드 가능합니다."),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "이미지 파일 크기가 너무 큽니다."),

    ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요를 눌렀습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 정보를 찾을 수 없습니다."),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "댓글에 접근할 권한이 없습니다."),
    COMMENT_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "댓글 내용은 필수입니다."),
    COMMENT_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "댓글 내용이 너무 깁니다.(최대 500자)"),
    REPLY_DEPTH_EXCEEDED(HttpStatus.BAD_REQUEST, "대댓글에는 답글을 달 수 없습니다."),

    HASHTAG_NOT_FOUND(HttpStatus.NOT_FOUND, "해시태그를 찾을 수 없습니다."),
    HASHTAG_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "해시태그 형식이 올바르지 않습니다."),
    HASHTAG_INVALID_PATTERN(HttpStatus.BAD_REQUEST, "해시태그는 한글, 영문, 숫자, 밑줄(_)만 사용 가능합니다."),
    HASHTAG_TOO_LONG(HttpStatus.BAD_REQUEST, "해시태그가 너무 깁니다.(최대 50자)"),
    HASHTAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "게시글당 해시태그는 최대 30개까지 등록 가능합니다."),

    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "북마크 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
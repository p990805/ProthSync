package com.prothsync.prothsync.entity.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    LIKE("님이 게시글에 좋아요를 눌렀습니다."),
    COMMENT("님이 게시글에 댓글을 남겼습니다."),
    FOLLOW("님이 회원님을 팔로우했습니다."),
    REVIEW_RECEIVED("님이 리뷰를 작성했습니다."),

    PROPOSAL_RECEIVED("님이 의뢰에 제안을 보냈습니다."),
    PROPOSAL_ACCEPTED("님이 제안을 수락했습니다."),
    PROPOSAL_REJECTED("님이 제안을 거절했습니다."),
    CASE_IN_PROGRESS("의뢰가 진행 중으로 변경되었습니다."),
    CASE_COMPLETED("의뢰가 완료되었습니다."),
    CASE_CANCELLED("의뢰가 취소되었습니다.");

    private final String message;
}
package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.review.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "리뷰 응답 DTO")
public record ReviewResponseDTO(

    @Schema(description = "리뷰 ID")
    Long reviewId,

    @Schema(description = "의뢰 ID")
    Long caseRequestId,

    @Schema(description = "작성자 ID")
    Long reviewerId,

    @Schema(description = "대상자 ID")
    Long revieweeId,

    @Schema(description = "평점")
    int rating,

    @Schema(description = "리뷰 내용")
    String content,

    @Schema(description = "작성일")
    LocalDateTime createdAt
) {
    public static ReviewResponseDTO from(Review review) {
        return new ReviewResponseDTO(
            review.getReviewId(),
            review.getCaseRequestId(),
            review.getReviewerId(),
            review.getRevieweeId(),
            review.getRating(),
            review.getContent(),
            review.getCreatedAt()
        );
    }
}
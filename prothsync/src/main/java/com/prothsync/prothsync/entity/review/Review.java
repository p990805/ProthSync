package com.prothsync.prothsync.entity.review;

import com.prothsync.prothsync.common.BaseEntity;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.ReviewErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_reviews_case_request",
            columnNames = {"case_request_id"}
        )
    }
)
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    private static final int MAX_CONTENT_LENGTH = 500;
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(name = "case_request_id", nullable = false)
    private Long caseRequestId;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "reviewee_id", nullable = false)
    private Long revieweeId;

    @Column(nullable = false)
    private int rating;

    @Column(length = 500)
    private String content;

    private Review(Long caseRequestId, Long reviewerId,
        Long revieweeId, int rating, String content) {
        this.caseRequestId = caseRequestId;
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.rating = rating;
        this.content = content;
    }

    public static Review create(Long caseRequestId, Long reviewerId,
        Long revieweeId, int rating, String content) {
        validateCaseRequestId(caseRequestId);
        validateReviewerId(reviewerId);
        validateRevieweeId(revieweeId);
        validateRating(rating);
        validateContent(content);

        return new Review(caseRequestId, reviewerId, revieweeId, rating, content);
    }


    private static void validateCaseRequestId(Long caseRequestId) {
        if (caseRequestId == null) {
            throw new BusinessException(ReviewErrorCode.REVIEW_CASE_REQUEST_ID_NULL);
        }
    }

    private static void validateReviewerId(Long reviewerId) {
        if (reviewerId == null) {
            throw new BusinessException(ReviewErrorCode.REVIEW_REVIEWER_ID_NULL);
        }
    }

    private static void validateRevieweeId(Long revieweeId) {
        if (revieweeId == null) {
            throw new BusinessException(ReviewErrorCode.REVIEW_REVIEWEE_ID_NULL);
        }
    }

    private static void validateRating(int rating) {
        if (rating < MIN_RATING || rating > MAX_RATING) {
            throw new BusinessException(ReviewErrorCode.REVIEW_RATING_INVALID);
        }
    }

    private static void validateContent(String content) {
        if (content != null && content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(ReviewErrorCode.REVIEW_CONTENT_TOO_LONG);
        }
    }


    public void updateContent(String newContent) {
        validateContent(newContent);
        this.content = newContent;
    }

    public void updateRating(int newRating) {
        validateRating(newRating);
        this.rating = newRating;
    }

    public boolean isWrittenBy(Long userId) {
        return this.reviewerId.equals(userId);
    }
}
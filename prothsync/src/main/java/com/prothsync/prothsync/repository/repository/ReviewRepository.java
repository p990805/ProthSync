package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.review.Review;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepository {

    Review save(Review review);

    Optional<Review> findById(Long reviewId);

    void softDelete(Review review, Long userId);

    boolean existsByCaseRequestId(Long caseRequestId);

    Page<Review> findAllByRevieweeId(Long revieweeId, Pageable pageable);

    Double findAverageRatingByRevieweeId(Long revieweeId);

    int countByRevieweeId(Long revieweeId);
}
package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

    boolean existsByCaseRequestId(Long caseRequestId);

    Page<Review> findAllByRevieweeIdOrderByCreatedAtDesc(Long revieweeId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.revieweeId = :revieweeId")
    Double findAverageRatingByRevieweeId(@Param("revieweeId") Long revieweeId);

    int countByRevieweeId(Long revieweeId);
}
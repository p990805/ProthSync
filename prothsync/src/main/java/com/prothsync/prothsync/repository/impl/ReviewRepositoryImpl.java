package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.review.Review;
import com.prothsync.prothsync.repository.jpa.ReviewJpaRepository;
import com.prothsync.prothsync.repository.repository.ReviewRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Review save(Review review) {
        return reviewJpaRepository.save(review);
    }

    @Override
    public Optional<Review> findById(Long reviewId) {
        return reviewJpaRepository.findById(reviewId);
    }

    @Override
    public void delete(Review review) {
        reviewJpaRepository.delete(review);
    }

    @Override
    public boolean existsByCaseRequestId(Long caseRequestId) {
        return reviewJpaRepository.existsByCaseRequestId(caseRequestId);
    }

    @Override
    public Page<Review> findAllByRevieweeId(Long revieweeId, Pageable pageable) {
        return reviewJpaRepository.findAllByRevieweeIdOrderByCreatedAtDesc(revieweeId, pageable);
    }

    @Override
    public Double findAverageRatingByRevieweeId(Long revieweeId) {
        return reviewJpaRepository.findAverageRatingByRevieweeId(revieweeId);
    }

    @Override
    public int countByRevieweeId(Long revieweeId) {
        return reviewJpaRepository.countByRevieweeId(revieweeId);
    }
}
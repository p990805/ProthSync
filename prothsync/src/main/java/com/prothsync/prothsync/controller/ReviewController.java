package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.ReviewControllerDocs;
import com.prothsync.prothsync.dto.ReviewCreateRequestDTO;
import com.prothsync.prothsync.dto.ReviewResponseDTO;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController implements ReviewControllerDocs {

    private final ReviewService reviewService;

    @PostMapping("/cases/{caseRequestId}/reviews")
    public ResponseEntity<ReviewResponseDTO> createReview(
        @PathVariable Long caseRequestId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody ReviewCreateRequestDTO request
    ) {
        ReviewResponseDTO response = reviewService.createReview(
            caseRequestId, userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<PageResponse<ReviewResponseDTO>> getReviewsByUser(
        @PathVariable Long userId,
        Pageable pageable
    ) {
        PageResponse<ReviewResponseDTO> response =
            reviewService.getReviewsByReviewee(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReview(
        @PathVariable Long reviewId
    ) {
        ReviewResponseDTO response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
        @PathVariable Long reviewId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody ReviewCreateRequestDTO request
    ) {
        ReviewResponseDTO response = reviewService.updateReview(
            reviewId, userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
        @PathVariable Long reviewId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        reviewService.deleteReview(reviewId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}
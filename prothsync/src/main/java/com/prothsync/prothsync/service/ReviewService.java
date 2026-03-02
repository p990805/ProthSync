package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.ReviewCreateRequestDTO;
import com.prothsync.prothsync.dto.ReviewResponseDTO;
import com.prothsync.prothsync.entity.caserequest.CaseRequest;
import com.prothsync.prothsync.entity.caserequest.CaseProposal;
import com.prothsync.prothsync.entity.caserequest.ProposalStatus;
import com.prothsync.prothsync.entity.notification.NotificationType;
import com.prothsync.prothsync.entity.notification.ReferenceType;
import com.prothsync.prothsync.entity.review.Review;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.ReviewErrorCode;
import com.prothsync.prothsync.exception.CaseErrorCode;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.CaseProposalRepository;
import com.prothsync.prothsync.repository.repository.CaseRequestRepository;
import com.prothsync.prothsync.repository.repository.ReviewRepository;
import com.prothsync.prothsync.repository.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CaseRequestRepository caseRequestRepository;
    private final CaseProposalRepository caseProposalRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * 리뷰 작성
     * 조건: 의뢰가 COMPLETED 상태 + 본인이 의뢰자 + 아직 리뷰 없음
     * 대상: 해당 의뢰에서 ACCEPTED된 제안의 proposer
     */
    @Transactional
    public ReviewResponseDTO createReview(
        Long caseRequestId, Long reviewerId, ReviewCreateRequestDTO request) {

        CaseRequest caseRequest = findCaseRequestOrThrow(caseRequestId);

        // 검증: 완료된 의뢰인지
        validateCaseCompleted(caseRequest);
        // 검증: 의뢰자 본인인지
        validateIsClient(caseRequest, reviewerId);
        // 검증: 중복 리뷰 방지
        validateNoExistingReview(caseRequestId);

        // 수락된 제안의 proposer(기공사) 찾기
        CaseProposal acceptedProposal = findAcceptedProposal(caseRequestId);
        Long revieweeId = acceptedProposal.getProposerId();

        Review review = Review.create(
            caseRequestId, reviewerId, revieweeId,
            request.rating(), request.content()
        );

        Review saved = reviewRepository.save(review);

        // 리뷰 대상자의 평균 평점 갱신
        updateUserRatingStats(revieweeId);

        // 알림 발송
        notificationService.send(
            NotificationType.REVIEW_RECEIVED, reviewerId, revieweeId,
            saved.getReviewId(), ReferenceType.REVIEW);

        return ReviewResponseDTO.from(saved);
    }

    /**
     * 특정 사용자가 받은 리뷰 목록 조회 (프로필에서 사용)
     */
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponseDTO> getReviewsByReviewee(
        Long revieweeId, Pageable pageable) {

        validateUserExists(revieweeId);

        Page<Review> page = reviewRepository.findAllByRevieweeId(revieweeId, pageable);

        List<ReviewResponseDTO> reviews = page.getContent().stream()
            .map(ReviewResponseDTO::from)
            .toList();

        return PageResponse.of(reviews, page);
    }

    /**
     * 리뷰 단건 조회
     */
    @Transactional(readOnly = true)
    public ReviewResponseDTO getReview(Long reviewId) {
        Review review = findReviewOrThrow(reviewId);
        return ReviewResponseDTO.from(review);
    }

    /**
     * 리뷰 수정 (작성자만 가능)
     */
    @Transactional
    public ReviewResponseDTO updateReview(
        Long reviewId, Long userId, ReviewCreateRequestDTO request) {

        Review review = findReviewOrThrow(reviewId);
        validateReviewOwner(review, userId);

        if (request.rating() != null) {
            review.updateRating(request.rating());
        }
        if (request.content() != null) {
            review.updateContent(request.content());
        }

        Review updated = reviewRepository.save(review);

        // 평점 변경 시 대상자의 평균 평점 갱신
        updateUserRatingStats(review.getRevieweeId());

        return ReviewResponseDTO.from(updated);
    }

    /**
     * 리뷰 삭제 (작성자만 가능)
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = findReviewOrThrow(reviewId);
        validateReviewOwner(review, userId);

        Long revieweeId = review.getRevieweeId();

        reviewRepository.softDelete(review, userId);

        // 삭제 후 대상자의 평균 평점 갱신
        updateUserRatingStats(revieweeId);
    }


    // === private 메서드 ===

    private void updateUserRatingStats(Long revieweeId) {
        User reviewee = userRepository.findById(revieweeId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Double avgRating = reviewRepository.findAverageRatingByRevieweeId(revieweeId);
        int reviewCount = reviewRepository.countByRevieweeId(revieweeId);

        reviewee.updateRatingStats(
            avgRating != null ? avgRating : 0.0,
            reviewCount
        );
        userRepository.save(reviewee);
    }

    private CaseRequest findCaseRequestOrThrow(Long caseRequestId) {
        return caseRequestRepository.findById(caseRequestId)
            .orElseThrow(() -> new BusinessException(CaseErrorCode.CASE_NOT_FOUND));
    }

    private Review findReviewOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    private CaseProposal findAcceptedProposal(Long caseRequestId) {
        List<CaseProposal> accepted = caseProposalRepository
            .findAllByCaseRequestIdAndStatus(caseRequestId, ProposalStatus.ACCEPTED);

        if (accepted.isEmpty()) {
            throw new BusinessException(CaseErrorCode.CASE_NO_ACCEPTED_PROPOSAL);
        }
        return accepted.get(0);
    }

    private void validateCaseCompleted(CaseRequest caseRequest) {
        if (caseRequest.getStatus() != com.prothsync.prothsync.entity.caserequest.CaseStatus.COMPLETED) {
            throw new BusinessException(ReviewErrorCode.REVIEW_CASE_NOT_COMPLETED);
        }
    }

    private void validateIsClient(CaseRequest caseRequest, Long userId) {
        if (!caseRequest.isOwner(userId)) {
            throw new BusinessException(ReviewErrorCode.REVIEW_NOT_CLIENT);
        }
    }

    private void validateNoExistingReview(Long caseRequestId) {
        if (reviewRepository.existsByCaseRequestId(caseRequestId)) {
            throw new BusinessException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }
    }

    private void validateReviewOwner(Review review, Long userId) {
        if (!review.isWrittenBy(userId)) {
            throw new BusinessException(ReviewErrorCode.REVIEW_ACCESS_DENIED);
        }
    }

    private void validateUserExists(Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}
package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.ReviewCreateRequestDTO;
import com.prothsync.prothsync.dto.ReviewResponseDTO;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Review", description = "리뷰/평점 관리 API")
public interface ReviewControllerDocs {

    @Operation(summary = "리뷰 작성", description = "완료된 의뢰에 대해 리뷰를 작성합니다.")
    @ApiResponse(responseCode = "201", description = "리뷰 생성 성공")
    @ApiResponse(responseCode = "409", description = "이미 리뷰가 존재하거나 의뢰가 완료 상태가 아님")
    ResponseEntity<ReviewResponseDTO> createReview(
        Long caseRequestId, CustomUserDetails userDetails,
        ReviewCreateRequestDTO request);

    @Operation(summary = "사용자 리뷰 목록 조회",
        description = "특정 사용자(기공사)가 받은 리뷰 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<PageResponse<ReviewResponseDTO>> getReviewsByUser(
        Long userId, Pageable pageable);

    @Operation(summary = "리뷰 단건 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<ReviewResponseDTO> getReview(Long reviewId);

    @Operation(summary = "리뷰 수정", description = "본인이 작성한 리뷰를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    ResponseEntity<ReviewResponseDTO> updateReview(
        Long reviewId, CustomUserDetails userDetails,
        ReviewCreateRequestDTO request);

    @Operation(summary = "리뷰 삭제", description = "본인이 작성한 리뷰를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    ResponseEntity<Void> deleteReview(
        Long reviewId, CustomUserDetails userDetails);
}
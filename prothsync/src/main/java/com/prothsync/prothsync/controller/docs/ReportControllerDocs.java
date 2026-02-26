package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.ReportCreateRequestDTO;
import com.prothsync.prothsync.dto.ReportResponseDTO;
import com.prothsync.prothsync.exception.ErrorResponse;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "신고", description = "신고 API")
@SecurityRequirement(name = "bearerAuth")
public interface ReportControllerDocs {

    @Operation(
        summary = "신고 접수",
        description = "게시글, 댓글, 사용자, 의뢰를 신고합니다. "
            + "자기 자신 또는 자신의 콘텐츠는 신고할 수 없으며, 동일 대상에 대해 중복 신고가 불가합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "신고 접수 성공",
            content = @Content(schema = @Schema(implementation = ReportResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "자기 자신을 신고할 수 없음 / 필수 값 누락",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "신고 대상을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 신고한 대상",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<ReportResponseDTO> createReport(
        CustomUserDetails userDetails,
        ReportCreateRequestDTO request
    );

    @Operation(
        summary = "내 신고 내역 조회",
        description = "내가 접수한 신고 목록을 페이징하여 조회합니다. 처리 상태(PENDING/ACCEPTED/REJECTED)를 확인할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<PageResponse<ReportResponseDTO>> getMyReports(
        CustomUserDetails userDetails,
        Pageable pageable
    );
}
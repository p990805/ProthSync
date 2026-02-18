package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.CaseProposalCreateDTO;
import com.prothsync.prothsync.dto.CaseProposalResponseDTO;
import com.prothsync.prothsync.exception.ErrorResponse;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "견적 제안", description = "외주 의뢰에 대한 견적 제안 API")
@SecurityRequirement(name = "bearerAuth")
public interface CaseProposalControllerDocs {

    @Operation(summary = "견적 제안 등록", description = "외주 의뢰에 견적 제안을 등록합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "제안 등록 성공",
            content = @Content(schema = @Schema(implementation = CaseProposalResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패 또는 본인 의뢰에 제안 불가",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "의뢰를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 제안을 보냈거나 모집중이 아닌 의뢰",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CaseProposalResponseDTO> createProposal(
        @Parameter(description = "의뢰 ID") Long caseRequestId,
        CustomUserDetails userDetails,
        CaseProposalCreateDTO request
    );

    @Operation(summary = "의뢰별 제안 목록 조회", description = "의뢰자가 본인의 의뢰에 들어온 제안 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음 (의뢰자만 조회 가능)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "의뢰를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<PageResponse<CaseProposalResponseDTO>> getProposalsByCaseRequest(
        @Parameter(description = "의뢰 ID") Long caseRequestId,
        CustomUserDetails userDetails,
        Pageable pageable
    );

    @Operation(summary = "내가 보낸 제안 목록 조회", description = "본인이 보낸 견적 제안 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<PageResponse<CaseProposalResponseDTO>> getMyProposals(
        CustomUserDetails userDetails,
        Pageable pageable
    );

    @Operation(summary = "제안 수락", description = "의뢰자가 제안을 수락합니다. 의뢰 상태가 진행중으로 변경되고 나머지 제안은 거절됩니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "수락 성공",
            content = @Content(schema = @Schema(implementation = CaseProposalResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "모집중이 아닌 의뢰 또는 이미 처리된 제안",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CaseProposalResponseDTO> acceptProposal(
        @Parameter(description = "제안 ID") Long proposalId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "제안 거절", description = "의뢰자가 제안을 거절합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "거절 성공",
            content = @Content(schema = @Schema(implementation = CaseProposalResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 처리된 제안",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CaseProposalResponseDTO> rejectProposal(
        @Parameter(description = "제안 ID") Long proposalId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "제안 삭제", description = "제안자가 본인의 PENDING 상태 제안을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 처리된 제안은 삭제 불가",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> deleteProposal(
        @Parameter(description = "제안 ID") Long proposalId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "의뢰 완료 처리", description = "의뢰자가 수락된 제안의 작업 완료를 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "완료 처리 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 종료된 의뢰",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> completeCaseRequest(
        @Parameter(description = "의뢰 ID") Long caseRequestId,
        CustomUserDetails userDetails
    );
}
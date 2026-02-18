package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.CaseRequestCreateDTO;
import com.prothsync.prothsync.dto.CaseRequestResponseDTO;
import com.prothsync.prothsync.dto.CaseRequestSummaryDTO;
import com.prothsync.prothsync.dto.CaseRequestUpdateDTO;
import com.prothsync.prothsync.dto.NearbyCaseResponseDTO;
import com.prothsync.prothsync.entity.caserequest.CaseCategory;
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
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "외주 의뢰", description = "외주 의뢰 CRUD 및 주변 검색 API")
@SecurityRequirement(name = "bearerAuth")
public interface CaseRequestControllerDocs {

    @Operation(summary = "외주 의뢰 등록", description = "새로운 외주 의뢰를 등록합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "의뢰 등록 성공",
            content = @Content(schema = @Schema(implementation = CaseRequestResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CaseRequestResponseDTO> createCaseRequest(
        CustomUserDetails userDetails,
        CaseRequestCreateDTO request
    );

    @Operation(summary = "외주 의뢰 상세 조회", description = "의뢰 ID로 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CaseRequestResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "의뢰를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CaseRequestResponseDTO> getCaseRequest(
        @Parameter(description = "의뢰 ID") Long caseRequestId
    );

    @Operation(summary = "모집중인 의뢰 목록 조회", description = "OPEN 상태의 의뢰를 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<PageResponse<CaseRequestSummaryDTO>> getOpenCaseRequests(Pageable pageable);

    @Operation(summary = "카테고리별 의뢰 목록 조회", description = "카테고리로 OPEN 상태 의뢰를 필터링하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<PageResponse<CaseRequestSummaryDTO>> getCaseRequestsByCategory(
        @Parameter(description = "카테고리") CaseCategory category,
        Pageable pageable
    );

    @Operation(summary = "내 의뢰 목록 조회", description = "본인이 등록한 의뢰 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<PageResponse<CaseRequestSummaryDTO>> getMyCaseRequests(
        CustomUserDetails userDetails,
        Pageable pageable
    );

    @Operation(summary = "외주 의뢰 수정", description = "본인이 등록한 OPEN 상태의 의뢰를 수정합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(schema = @Schema(implementation = CaseRequestResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "의뢰를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "모집중인 의뢰가 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CaseRequestResponseDTO> updateCaseRequest(
        @Parameter(description = "의뢰 ID") Long caseRequestId,
        CustomUserDetails userDetails,
        CaseRequestUpdateDTO request
    );

    @Operation(summary = "외주 의뢰 취소", description = "본인이 등록한 의뢰를 취소합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "취소 성공"),
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
    ResponseEntity<Void> cancelCaseRequest(
        @Parameter(description = "의뢰 ID") Long caseRequestId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "외주 의뢰 삭제", description = "본인이 등록한 의뢰와 관련 제안을 모두 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "의뢰를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> deleteCaseRequest(
        @Parameter(description = "의뢰 ID") Long caseRequestId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "주변 외주 의뢰 검색", description = "현재 사용자 위치 기준 주변 OPEN 상태 의뢰를 검색합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "좌표 미설정 또는 잘못된 검색 반경",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<List<NearbyCaseResponseDTO>> getNearbyCases(
        CustomUserDetails userDetails,
        @Parameter(description = "검색 반경 (km, 기본 10)") Double radiusKm,
        @Parameter(description = "최대 결과 수 (기본 20)") Integer limit
    );

    @Operation(summary = "카테고리별 주변 외주 의뢰 검색", description = "카테고리 필터를 포함한 주변 OPEN 의뢰 검색입니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "좌표 미설정 또는 잘못된 검색 반경",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<List<NearbyCaseResponseDTO>> getNearbyCasesByCategory(
        CustomUserDetails userDetails,
        @Parameter(description = "카테고리") CaseCategory category,
        @Parameter(description = "검색 반경 (km, 기본 10)") Double radiusKm,
        @Parameter(description = "최대 결과 수 (기본 20)") Integer limit
    );
}
package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.AdminDashboardResponseDTO;
import com.prothsync.prothsync.dto.AdminUserResponseDTO;
import com.prothsync.prothsync.dto.ReportProcessRequestDTO;
import com.prothsync.prothsync.dto.ReportResponseDTO;
import com.prothsync.prothsync.dto.UserRoleUpdateRequestDTO;
import com.prothsync.prothsync.dto.UserSuspendRequestDTO;
import com.prothsync.prothsync.entity.report.ReportStatus;
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

@Tag(name = "관리자", description = "관리자 전용 API (ADMIN 권한 필요)")
@SecurityRequirement(name = "bearerAuth")
public interface AdminControllerDocs {


    @Operation(summary = "신고 목록 조회", description = "신고 목록을 상태별로 필터링하여 조회합니다. 상태 미지정 시 PENDING 목록을 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "403", description = "관리자 권한 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<PageResponse<ReportResponseDTO>> getReports(
        @Parameter(description = "신고 상태 필터 (PENDING, ACCEPTED, REJECTED)") ReportStatus status,
        Pageable pageable
    );

    @Operation(summary = "신고 상세 조회", description = "특정 신고의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ReportResponseDTO> getReport(
        @Parameter(description = "신고 ID") Long reportId
    );

    @Operation(summary = "신고 처리", description = "신고를 수락(ACCEPTED) 또는 거절(REJECTED)로 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "처리 성공"),
        @ApiResponse(responseCode = "400", description = "이미 처리된 신고 / 잘못된 상태값",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ReportResponseDTO> processReport(
        @Parameter(description = "신고 ID") Long reportId,
        CustomUserDetails userDetails,
        ReportProcessRequestDTO request
    );


    @Operation(summary = "전체 사용자 목록 조회", description = "전체 사용자를 페이징하여 조회합니다. 정지 상태, 권한 정보가 포함됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<PageResponse<AdminUserResponseDTO>> getUsers(Pageable pageable);

    @Operation(summary = "계정 정지", description = "대상 사용자의 계정을 정지합니다. 관리자 계정은 정지할 수 없습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정지 성공"),
        @ApiResponse(responseCode = "400", description = "관리자 계정 정지 불가 / 이미 정지된 사용자",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<AdminUserResponseDTO> suspendUser(
        @Parameter(description = "대상 사용자 ID") Long userId,
        UserSuspendRequestDTO request
    );

    @Operation(summary = "정지 해제", description = "정지된 사용자의 계정을 복원합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "해제 성공"),
        @ApiResponse(responseCode = "400", description = "정지 상태가 아닌 사용자",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<AdminUserResponseDTO> unsuspendUser(
        @Parameter(description = "대상 사용자 ID") Long userId
    );

    @Operation(summary = "사용자 권한 변경", description = "사용자의 권한을 ADMIN 또는 USER로 변경합니다. 자신의 권한은 변경할 수 없습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "변경 성공"),
        @ApiResponse(responseCode = "400", description = "자신의 권한 변경 불가",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<AdminUserResponseDTO> updateUserRole(
        @Parameter(description = "대상 사용자 ID") Long userId,
        CustomUserDetails userDetails,
        UserRoleUpdateRequestDTO request
    );


    @Operation(summary = "게시글 강제 삭제", description = "관리자 권한으로 게시글을 삭제합니다. 관련 좋아요, 북마크, 댓글, 해시태그, 이미지가 함께 삭제됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deletePost(@Parameter(description = "게시글 ID") Long postId);

    @Operation(summary = "댓글 강제 삭제", description = "관리자 권한으로 댓글을 삭제합니다. 게시글의 댓글 수가 자동으로 감소합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteComment(@Parameter(description = "댓글 ID") Long commentId);

    @Operation(summary = "의뢰 강제 삭제", description = "관리자 권한으로 의뢰를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "의뢰를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteCaseRequest(@Parameter(description = "의뢰 ID") Long caseRequestId);


    @Operation(summary = "대시보드 통계 조회", description = "총 사용자 수, 게시글 수, 의뢰 수, 신고 현황 등 플랫폼 전체 통계를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<AdminDashboardResponseDTO> getDashboardStats();
}
package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.NotificationResponseDTO;
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

@Tag(name = "알림", description = "알림 API")
@SecurityRequirement(name = "bearerAuth")
public interface NotificationControllerDocs {

    @Operation(summary = "내 알림 목록 조회", description = "현재 사용자의 알림 목록을 최신순으로 페이징 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<PageResponse<NotificationResponseDTO>> getMyNotifications(
        CustomUserDetails userDetails,
        Pageable pageable
    );

    @Operation(summary = "읽지 않은 알림 수 조회", description = "현재 사용자의 읽지 않은 알림 수를 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<Integer> getUnreadCount(CustomUserDetails userDetails);

    @Operation(summary = "개별 알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "읽음 처리 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "알림에 접근할 권한이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "알림을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> markAsRead(
        @Parameter(description = "알림 ID") Long notificationId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "전체 알림 읽음 처리", description = "현재 사용자의 모든 알림을 읽음 상태로 변경합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "전체 읽음 처리 성공")
    })
    ResponseEntity<Integer> markAllAsRead(CustomUserDetails userDetails);

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "알림에 접근할 권한이 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "알림을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> deleteNotification(
        @Parameter(description = "알림 ID") Long notificationId,
        CustomUserDetails userDetails
    );
}
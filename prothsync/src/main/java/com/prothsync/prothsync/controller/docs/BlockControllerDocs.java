package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.BlockResponseDTO;
import com.prothsync.prothsync.dto.BlockedUserResponseDTO;
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

@Tag(name = "차단", description = "사용자 차단 API")
@SecurityRequirement(name = "bearerAuth")
public interface BlockControllerDocs {

    @Operation(summary = "사용자 차단", description = "대상 사용자를 차단합니다. 차단 시 양방향 팔로우 관계가 자동으로 해제됩니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "차단 성공",
            content = @Content(schema = @Schema(implementation = BlockResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "자기 자신을 차단할 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 차단한 사용자",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<BlockResponseDTO> block(
        @Parameter(description = "차단할 사용자 ID") Long userId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "차단 해제", description = "대상 사용자의 차단을 해제합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "차단 해제 성공",
            content = @Content(schema = @Schema(implementation = BlockResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "차단 정보를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<BlockResponseDTO> unblock(
        @Parameter(description = "차단 해제할 사용자 ID") Long userId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "차단 목록 조회", description = "내가 차단한 사용자 목록을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<PageResponse<BlockedUserResponseDTO>> getBlockedUsers(
        CustomUserDetails userDetails,
        Pageable pageable
    );

    @Operation(summary = "차단 여부 확인", description = "대상 사용자를 차단하고 있는지 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<Boolean> isBlocked(
        @Parameter(description = "대상 사용자 ID") Long userId,
        CustomUserDetails userDetails
    );
}
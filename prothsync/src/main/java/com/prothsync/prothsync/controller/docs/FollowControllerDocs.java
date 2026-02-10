package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.FollowResponseDTO;
import com.prothsync.prothsync.dto.FollowUserResponseDTO;
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

@Tag(name = "팔로우", description = "팔로우 API")
@SecurityRequirement(name = "bearerAuth")
public interface FollowControllerDocs {

    @Operation(summary = "팔로우 토글", description = "대상 사용자를 팔로우하거나 언팔로우합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "팔로우 토글 성공",
            content = @Content(schema = @Schema(implementation = FollowResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "자기 자신을 팔로우할 수 없음",
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
        )
    })
    ResponseEntity<FollowResponseDTO> toggleFollow(
        @Parameter(description = "대상 사용자 ID") Long userId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "팔로우 여부 확인", description = "현재 사용자가 대상 사용자를 팔로우하고 있는지 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<Boolean> isFollowing(
        @Parameter(description = "대상 사용자 ID") Long userId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "팔로워 목록 조회", description = "대상 사용자의 팔로워 목록을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<PageResponse<FollowUserResponseDTO>> getFollowers(
        @Parameter(description = "대상 사용자 ID") Long userId,
        Pageable pageable
    );

    @Operation(summary = "팔로잉 목록 조회", description = "대상 사용자의 팔로잉 목록을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<PageResponse<FollowUserResponseDTO>> getFollowings(
        @Parameter(description = "대상 사용자 ID") Long userId,
        Pageable pageable
    );
}
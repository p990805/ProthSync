package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.PostResponseDTO;
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

@Tag(name = "피드", description = "피드 조회 API")
@SecurityRequirement(name = "bearerAuth")
public interface FeedControllerDocs {

    @Operation(
        summary = "팔로잉 피드 조회",
        description = "현재 사용자가 팔로우한 사용자들의 공개 게시글을 최신순으로 조회합니다. "
            + "팔로우한 사용자가 없는 경우 빈 결과를 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "피드 조회 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<PageResponse<PostResponseDTO>> getFollowingFeed(
        CustomUserDetails userDetails,
        Pageable pageable
    );
}
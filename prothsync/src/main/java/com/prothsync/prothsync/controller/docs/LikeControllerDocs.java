package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.LikeResponseDTO;
import com.prothsync.prothsync.exception.ErrorResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "좋아요", description = "게시글 좋아요 API")
@SecurityRequirement(name = "bearerAuth")
public interface LikeControllerDocs {

    @Operation(summary = "좋아요 토글", description = "게시글에 좋아요를 누르거나 취소합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "좋아요 토글 성공",
            content = @Content(schema = @Schema(implementation = LikeResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<LikeResponseDTO> toggleLike(
        @Parameter(description = "게시글 ID") Long postId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "좋아요 여부 확인", description = "현재 사용자가 해당 게시글에 좋아요를 눌렀는지 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<Boolean> isLiked(
        @Parameter(description = "게시글 ID") Long postId,
        CustomUserDetails userDetails
    );
}
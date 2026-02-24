package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.BookmarkResponseDTO;
import com.prothsync.prothsync.dto.PostResponseDTO;
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

@Tag(name = "북마크", description = "게시글 북마크(스크랩) API")
@SecurityRequirement(name = "bearerAuth")
public interface BookmarkControllerDocs {

    @Operation(summary = "북마크 토글", description = "게시글을 북마크하거나 해제합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "북마크 토글 성공",
            content = @Content(schema = @Schema(implementation = BookmarkResponseDTO.class))
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
    ResponseEntity<BookmarkResponseDTO> toggleBookmark(
        @Parameter(description = "게시글 ID") Long postId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "내 북마크 목록 조회", description = "현재 사용자가 북마크한 게시글 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<PageResponse<PostResponseDTO>> getMyBookmarks(
        CustomUserDetails userDetails,
        Pageable pageable
    );

    @Operation(summary = "북마크 여부 확인", description = "현재 사용자가 해당 게시글을 북마크했는지 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<Boolean> isBookmarked(
        @Parameter(description = "게시글 ID") Long postId,
        CustomUserDetails userDetails
    );
}
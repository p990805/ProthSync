package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.CommentCreateRequestDTO;
import com.prothsync.prothsync.dto.CommentResponseDTO;
import com.prothsync.prothsync.dto.CommentUpdateRequestDTO;
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

@Tag(name = "댓글", description = "댓글 CRUD API")
@SecurityRequirement(name = "bearerAuth")
public interface CommentControllerDocs {

    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "댓글 작성 성공",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CommentResponseDTO> createComment(
        @Parameter(description = "게시글 ID") Long postId,
        CustomUserDetails userDetails,
        CommentCreateRequestDTO request
    );

    @Operation(summary = "댓글 목록 조회", description = "게시글의 댓글을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<PageResponse<CommentResponseDTO>> getComments(
        @Parameter(description = "게시글 ID") Long postId,
        Pageable pageable
    );

    @Operation(summary = "댓글 수정", description = "본인이 작성한 댓글을 수정합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "댓글 수정 성공",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
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
            description = "댓글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CommentResponseDTO> updateComment(
        @Parameter(description = "댓글 ID") Long commentId,
        CustomUserDetails userDetails,
        CommentUpdateRequestDTO request
    );

    @Operation(summary = "댓글 삭제", description = "본인이 작성한 댓글을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "댓글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> deleteComment(
        @Parameter(description = "댓글 ID") Long commentId,
        CustomUserDetails userDetails
    );
}
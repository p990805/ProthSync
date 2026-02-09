package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.PostCreateRequestDTO;
import com.prothsync.prothsync.dto.PostResponseDTO;
import com.prothsync.prothsync.dto.PostSummaryResponseDTO;
import com.prothsync.prothsync.dto.PostUpdateRequestDTO;
import com.prothsync.prothsync.entity.post.PostCategory;
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

@Tag(name = "게시글", description = "게시글 CRUD API")
@SecurityRequirement(name = "bearerAuth")
public interface PostControllerDocs {

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "게시글 작성 성공",
            content = @Content(schema = @Schema(implementation = PostResponseDTO.class))
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
    ResponseEntity<PostResponseDTO> createPost(
        CustomUserDetails userDetails,
        PostCreateRequestDTO request
    );

    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 단건 조회합니다. 비공개 게시글은 작성자만 조회 가능합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "게시글 조회 성공",
            content = @Content(schema = @Schema(implementation = PostResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<PostResponseDTO> getPost(
        @Parameter(description = "게시글 ID") Long postId,
        CustomUserDetails userDetails
    );

    @Operation(summary = "전체 공개 게시글 목록 조회", description = "공개 게시글을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<PageResponse<PostSummaryResponseDTO>> getPublicPosts(Pageable pageable);

    @Operation(summary = "사용자별 게시글 목록 조회", description = "특정 사용자의 게시글을 조회합니다. 본인이면 전체, 타인이면 공개 게시글만 조회됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<PageResponse<PostSummaryResponseDTO>> getUserPosts(
        @Parameter(description = "대상 사용자 ID") Long userId,
        CustomUserDetails userDetails,
        Pageable pageable
    );

    @Operation(summary = "카테고리별 게시글 목록 조회", description = "카테고리로 게시글을 필터링하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<PageResponse<PostSummaryResponseDTO>> getPostsByCategory(
        @Parameter(description = "카테고리") PostCategory category,
        Pageable pageable
    );

    @Operation(summary = "게시글 수정", description = "본인이 작성한 게시글을 수정합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "게시글 수정 성공",
            content = @Content(schema = @Schema(implementation = PostResponseDTO.class))
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
            description = "게시글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<PostResponseDTO> updatePost(
        @Parameter(description = "게시글 ID") Long postId,
        CustomUserDetails userDetails,
        PostUpdateRequestDTO request
    );

    @Operation(summary = "게시글 삭제", description = "본인이 작성한 게시글을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<Void> deletePost(
        @Parameter(description = "게시글 ID") Long postId,
        CustomUserDetails userDetails
    );
}
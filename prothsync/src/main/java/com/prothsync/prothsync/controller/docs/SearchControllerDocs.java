package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.HashtagResponseDTO;
import com.prothsync.prothsync.dto.PostSummaryResponseDTO;
import com.prothsync.prothsync.dto.UserSearchResponseDTO;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "검색", description = "해시태그 및 사용자 검색 API")
public interface SearchControllerDocs {

    @Operation(
        summary = "해시태그 자동완성 검색",
        description = "키워드로 시작하는 해시태그를 사용량 순으로 조회합니다. '#' 접두사는 자동으로 제거됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "400", description = "검색 키워드가 비어있거나 너무 깁니다")
    })
    ResponseEntity<PageResponse<HashtagResponseDTO>> searchHashtags(
        @Parameter(description = "검색 키워드 (예: 임플, 크라운)", required = true) String keyword,
        Pageable pageable
    );

    @Operation(
        summary = "해시태그로 게시물 검색",
        description = "특정 해시태그가 달린 공개 게시물을 최신순으로 조회합니다. "
            + "로그인 시 차단한 사용자의 게시물은 제외됩니다. '#' 접두사는 자동으로 제거됩니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "400", description = "검색 키워드가 비어있거나 너무 깁니다"),
        @ApiResponse(responseCode = "404", description = "해당 해시태그가 존재하지 않습니다")
    })
    ResponseEntity<PageResponse<PostSummaryResponseDTO>> searchPostsByHashtag(
        @Parameter(description = "해시태그 이름 (예: 임플란트, 크라운)", required = true) String hashtag,
        CustomUserDetails userDetails,
        Pageable pageable
    );

    @Operation(
        summary = "사용자 닉네임 검색",
        description = "닉네임에 키워드가 포함된 사용자를 조회합니다. "
            + "로그인 시 차단한 사용자는 결과에서 제외됩니다. 대소문자를 구분하지 않습니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "400", description = "검색 키워드가 비어있거나 너무 깁니다")
    })
    ResponseEntity<PageResponse<UserSearchResponseDTO>> searchUsers(
        @Parameter(description = "검색 키워드 (닉네임)", required = true) String keyword,
        CustomUserDetails userDetails,
        Pageable pageable
    );
}
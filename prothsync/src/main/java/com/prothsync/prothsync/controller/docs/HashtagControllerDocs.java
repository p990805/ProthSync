package com.prothsync.prothsync.controller.docs;

import com.prothsync.prothsync.dto.HashtagResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "해시태그", description = "해시태그 조회 API")
public interface HashtagControllerDocs {

    @Operation(summary = "인기 해시태그 조회", description = "사용량 기준 상위 해시태그를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<List<HashtagResponseDTO>> getTrendingHashtags(
        @Parameter(description = "조회 개수 (기본값: 10)") int limit
    );
}
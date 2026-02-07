package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.post.PostCategory;
import com.prothsync.prothsync.entity.post.PostVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "게시글 수정 요청 DTO")
public record PostUpdateRequestDTO(

    @Schema(description = "게시글 내용")
    @Size(max = 2000, message = "게시글 내용은 2000자를 초과할 수 없습니다.")
    String content,

    @Schema(description = "카테고리")
    PostCategory category,

    @Schema(description = "공개 범위")
    PostVisibility visibility,

    @Schema(description = "이미지 URL 목록 (전체 교체)")
    @Size(max = 10, message = "이미지는 최대 10개까지 등록 가능합니다.")
    List<String> imageUrls,

    @Schema(description = "해시태그 목록 (전체 교체)")
    @Size(max = 30, message = "해시태그는 최대 30개까지 등록 가능합니다.")
    List<String> hashtagNames
) {
}

package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.post.PostCategory;
import com.prothsync.prothsync.entity.post.PostVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "게시글 생성 요청 DTO")
public record PostCreateRequestDTO(

    @Schema(description = "게시글 내용", example = "오늘 작업한 크라운 보철물입니다.")
    @Size(max = 2000, message = "게시글 내용은 2000자를 초과할 수 없습니다.")
    String content,

    @Schema(description = "카테고리", example = "WORK_SHOWCASE")
    @NotNull(message = "카테고리는 필수입니다.")
    PostCategory category,

    @Schema(description = "공개 범위", example = "PUBLIC")
    @NotNull(message = "공개 범위는 필수입니다.")
    PostVisibility visibility,

    @Schema(description = "이미지 URL 목록")
    @Size(max = 10, message = "이미지는 최대 10개까지 등록 가능합니다.")
    List<String> imageUrls,

    @Schema(description = "해시태그 목록", example = "[\"크라운\", \"보철\"]")
    @Size(max = 30, message = "해시태그는 최대 30개까지 등록 가능합니다.")
    List<String> hashtagNames
) {
}

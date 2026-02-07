package com.prothsync.prothsync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "댓글 생성 요청 DTO")
public record CommentCreateRequestDTO(

    @Schema(description = "댓글 내용", example = "정말 깔끔한 작업이네요!")
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 500, message = "댓글 내용은 500자를 초과할 수 없습니다.")
    String content
) {
}

package com.prothsync.prothsync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "댓글 수정 요청 DTO")
public record CommentUpdateRequestDTO(

    @Schema(description = "댓글 내용")
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 500, message = "댓글 내용은 500자를 초과할 수 없습니다.")
    String content
) {
}

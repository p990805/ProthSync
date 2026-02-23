package com.prothsync.prothsync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "리뷰 생성 요청 DTO")
public record ReviewCreateRequestDTO(

    @Schema(description = "평점 (1~5)", example = "5")
    @NotNull(message = "평점은 필수입니다.")
    @Min(value = 1, message = "평점은 최소 1점입니다.")
    @Max(value = 5, message = "평점은 최대 5점입니다.")
    Integer rating,

    @Schema(description = "리뷰 내용 (최대 500자)", example = "보철물 퀄리티가 매우 좋았습니다.")
    @Size(max = 500, message = "리뷰 내용은 최대 500자입니다.")
    String content
) {}
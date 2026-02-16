package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.caserequest.CaseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "외주 의뢰 생성 요청 DTO")
public record CaseRequestCreateDTO(

    @Schema(description = "의뢰 제목", example = "지르코니아 크라운 3개 제작 의뢰")
    @NotBlank(message = "의뢰 제목은 필수입니다.")
    @Size(max = 100, message = "의뢰 제목은 100자를 초과할 수 없습니다.")
    String title,

    @Schema(description = "의뢰 설명", example = "상악 1, 2, 3번 지르코니아 크라운 제작 부탁드립니다.")
    @Size(max = 3000, message = "의뢰 설명은 3000자를 초과할 수 없습니다.")
    String description,

    @Schema(description = "카테고리", example = "CROWN")
    @NotNull(message = "카테고리는 필수입니다.")
    CaseCategory category,

    @Schema(description = "희망 납기일", example = "2026-03-01")
    LocalDate preferredDeadline,

    @Schema(description = "예산 (원)", example = "150000")
    @Positive(message = "예산은 0보다 커야 합니다.")
    Long budget,

    @Schema(description = "치식 번호", example = "11,12,13")
    String toothNumbers
) {
}
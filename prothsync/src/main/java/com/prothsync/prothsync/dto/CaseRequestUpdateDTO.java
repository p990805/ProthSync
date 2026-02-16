package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.caserequest.CaseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "외주 의뢰 수정 요청 DTO")
public record CaseRequestUpdateDTO(

    @Schema(description = "의뢰 제목")
    @Size(max = 100, message = "의뢰 제목은 100자를 초과할 수 없습니다.")
    String title,

    @Schema(description = "의뢰 설명")
    @Size(max = 3000, message = "의뢰 설명은 3000자를 초과할 수 없습니다.")
    String description,

    @Schema(description = "카테고리")
    CaseCategory category,

    @Schema(description = "희망 납기일")
    LocalDate preferredDeadline,

    @Schema(description = "예산 (원)")
    @Positive(message = "예산은 0보다 커야 합니다.")
    Long budget,

    @Schema(description = "치식 번호")
    String toothNumbers
) {
}
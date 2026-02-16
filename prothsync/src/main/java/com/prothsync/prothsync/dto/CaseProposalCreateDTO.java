package com.prothsync.prothsync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "견적 제안 생성 요청 DTO")
public record CaseProposalCreateDTO(

    @Schema(description = "제안 메시지", example = "안녕하세요, 10년 경력 기공사입니다. 고품질 작업 약속드립니다.")
    @Size(max = 1000, message = "제안 메시지는 1000자를 초과할 수 없습니다.")
    String message,

    @Schema(description = "예상 금액 (원)", example = "120000")
    @NotNull(message = "예상 금액은 필수입니다.")
    @Positive(message = "예상 금액은 0보다 커야 합니다.")
    Long estimatedPrice,

    @Schema(description = "예상 소요일", example = "5")
    @NotNull(message = "예상 소요일은 필수입니다.")
    @Min(value = 1, message = "예상 소요일은 1일 이상이어야 합니다.")
    Integer estimatedDays
) {
}
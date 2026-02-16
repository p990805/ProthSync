package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.caserequest.CaseCategory;
import com.prothsync.prothsync.entity.caserequest.CaseRequest;
import com.prothsync.prothsync.entity.caserequest.CaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "외주 의뢰 상세 응답 DTO")
public record CaseRequestResponseDTO(

    @Schema(description = "의뢰 ID")
    Long caseRequestId,

    @Schema(description = "의뢰자 ID")
    Long clientId,

    @Schema(description = "의뢰 제목")
    String title,

    @Schema(description = "의뢰 설명")
    String description,

    @Schema(description = "카테고리")
    CaseCategory category,

    @Schema(description = "상태")
    CaseStatus status,

    @Schema(description = "희망 납기일")
    LocalDate preferredDeadline,

    @Schema(description = "예산 (원)")
    Long budget,

    @Schema(description = "치식 번호")
    String toothNumbers,

    @Schema(description = "받은 제안 수")
    int proposalCount,

    @Schema(description = "위도")
    Double latitude,

    @Schema(description = "경도")
    Double longitude,

    @Schema(description = "생성일시")
    LocalDateTime createdAt,

    @Schema(description = "수정일시")
    LocalDateTime updatedAt
) {

    public static CaseRequestResponseDTO from(CaseRequest caseRequest) {
        return new CaseRequestResponseDTO(
            caseRequest.getCaseRequestId(),
            caseRequest.getClientId(),
            caseRequest.getTitle(),
            caseRequest.getDescription(),
            caseRequest.getCategory(),
            caseRequest.getStatus(),
            caseRequest.getPreferredDeadline(),
            caseRequest.getBudget(),
            caseRequest.getToothNumbers(),
            caseRequest.getProposalCount(),
            caseRequest.getLatitude(),
            caseRequest.getLongitude(),
            caseRequest.getCreatedAt(),
            caseRequest.getUpdatedAt()
        );
    }
}
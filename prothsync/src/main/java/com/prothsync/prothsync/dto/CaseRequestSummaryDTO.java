package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.caserequest.CaseCategory;
import com.prothsync.prothsync.entity.caserequest.CaseRequest;
import com.prothsync.prothsync.entity.caserequest.CaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "외주 의뢰 요약 응답 DTO")
public record CaseRequestSummaryDTO(

    @Schema(description = "의뢰 ID")
    Long caseRequestId,

    @Schema(description = "의뢰자 ID")
    Long clientId,

    @Schema(description = "의뢰 제목")
    String title,

    @Schema(description = "카테고리")
    CaseCategory category,

    @Schema(description = "상태")
    CaseStatus status,

    @Schema(description = "희망 납기일")
    LocalDate preferredDeadline,

    @Schema(description = "예산 (원)")
    Long budget,

    @Schema(description = "받은 제안 수")
    int proposalCount,

    @Schema(description = "생성일시")
    LocalDateTime createdAt
) {

    public static CaseRequestSummaryDTO from(CaseRequest caseRequest) {
        return new CaseRequestSummaryDTO(
            caseRequest.getCaseRequestId(),
            caseRequest.getClientId(),
            caseRequest.getTitle(),
            caseRequest.getCategory(),
            caseRequest.getStatus(),
            caseRequest.getPreferredDeadline(),
            caseRequest.getBudget(),
            caseRequest.getProposalCount(),
            caseRequest.getCreatedAt()
        );
    }
}
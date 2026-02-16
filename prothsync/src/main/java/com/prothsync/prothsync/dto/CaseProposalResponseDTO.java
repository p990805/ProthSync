package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.caserequest.CaseProposal;
import com.prothsync.prothsync.entity.caserequest.ProposalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "견적 제안 응답 DTO")
public record CaseProposalResponseDTO(

    @Schema(description = "제안 ID")
    Long proposalId,

    @Schema(description = "의뢰 ID")
    Long caseRequestId,

    @Schema(description = "제안자 ID")
    Long proposerId,

    @Schema(description = "제안 메시지")
    String message,

    @Schema(description = "예상 금액 (원)")
    Long estimatedPrice,

    @Schema(description = "예상 소요일")
    int estimatedDays,

    @Schema(description = "제안 상태")
    ProposalStatus status,

    @Schema(description = "생성일시")
    LocalDateTime createdAt,

    @Schema(description = "수정일시")
    LocalDateTime updatedAt
) {

    public static CaseProposalResponseDTO from(CaseProposal proposal) {
        return new CaseProposalResponseDTO(
            proposal.getProposalId(),
            proposal.getCaseRequestId(),
            proposal.getProposerId(),
            proposal.getMessage(),
            proposal.getEstimatedPrice(),
            proposal.getEstimatedDays(),
            proposal.getStatus(),
            proposal.getCreatedAt(),
            proposal.getUpdatedAt()
        );
    }
}
package com.prothsync.prothsync.entity.caserequest;

import com.prothsync.prothsync.common.BaseEntity;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.CaseErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "case_proposals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CaseProposal extends BaseEntity {

    private static final int MAX_MESSAGE_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proposalId;

    @Column(name = "case_request_id", nullable = false)
    private Long caseRequestId;

    @Column(name = "proposer_id", nullable = false)
    private Long proposerId;

    @Column(length = 1000)
    private String message;

    @Column(nullable = false)
    private Long estimatedPrice;

    @Column(nullable = false)
    private int estimatedDays;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProposalStatus status;

    private CaseProposal(Long caseRequestId, Long proposerId,
        String message, Long estimatedPrice, int estimatedDays) {
        this.caseRequestId = caseRequestId;
        this.proposerId = proposerId;
        this.message = message;
        this.estimatedPrice = estimatedPrice;
        this.estimatedDays = estimatedDays;
        this.status = ProposalStatus.PENDING;
    }

    public static CaseProposal create(Long caseRequestId, Long proposerId,
        String message, Long estimatedPrice, int estimatedDays) {
        validateMessage(message);
        validatePrice(estimatedPrice);
        validateDays(estimatedDays);

        return new CaseProposal(caseRequestId, proposerId,
            message, estimatedPrice, estimatedDays);
    }


    private static void validateMessage(String message) {
        if (message != null && message.length() > MAX_MESSAGE_LENGTH) {
            throw new BusinessException(CaseErrorCode.PROPOSAL_MESSAGE_TOO_LONG);
        }
    }

    private static void validatePrice(Long price) {
        if (price == null || price <= 0) {
            throw new BusinessException(CaseErrorCode.PROPOSAL_PRICE_INVALID);
        }
    }

    private static void validateDays(int days) {
        if (days < 1) {
            throw new BusinessException(CaseErrorCode.PROPOSAL_DAYS_INVALID);
        }
    }


    public void accept() {
        validateNotDecided();
        this.status = ProposalStatus.ACCEPTED;
    }

    public void reject() {
        validateNotDecided();
        this.status = ProposalStatus.REJECTED;
    }

    private void validateNotDecided() {
        if (this.status != ProposalStatus.PENDING) {
            throw new BusinessException(CaseErrorCode.PROPOSAL_ALREADY_DECIDED);
        }
    }


    public void updateMessage(String message) {
        validateMessage(message);
        validateNotDecided();
        this.message = message;
    }

    public void updateEstimatedPrice(Long price) {
        validatePrice(price);
        validateNotDecided();
        this.estimatedPrice = price;
    }

    public void updateEstimatedDays(int days) {
        validateDays(days);
        validateNotDecided();
        this.estimatedDays = days;
    }


    public boolean isOwnedBy(Long userId) {
        return this.proposerId.equals(userId);
    }

    public boolean isPending() {
        return this.status == ProposalStatus.PENDING;
    }

    public boolean isAccepted() {
        return this.status == ProposalStatus.ACCEPTED;
    }
}
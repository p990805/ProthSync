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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "case_requests")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CaseRequest extends BaseEntity {

    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 3000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caseRequestId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 3000)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CaseCategory category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    private LocalDate preferredDeadline;

    private Long budget;

    @Column(length = 200)
    private String toothNumbers;

    @Column(nullable = false)
    private int proposalCount = 0;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    private CaseRequest(Long clientId, String title, String description,
        CaseCategory category, LocalDate preferredDeadline,
        Long budget, String toothNumbers,
        Double latitude, Double longitude) {
        this.clientId = clientId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = CaseStatus.OPEN;
        this.preferredDeadline = preferredDeadline;
        this.budget = budget;
        this.toothNumbers = toothNumbers;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static CaseRequest create(Long clientId, String title, String description,
        CaseCategory category, LocalDate preferredDeadline,
        Long budget, String toothNumbers,
        Double latitude, Double longitude) {
        validateClientId(clientId);
        validateTitle(title);
        validateDescription(description);
        validateCategory(category);
        validateDeadline(preferredDeadline);
        validateBudget(budget);

        return new CaseRequest(clientId, title, description,
            category, preferredDeadline, budget, toothNumbers,
            latitude, longitude);
    }


    private static void validateClientId(Long clientId) {
        if (clientId == null) {
            throw new BusinessException(CaseErrorCode.CASE_CLIENT_ID_IS_NULL);
        }
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BusinessException(CaseErrorCode.CASE_TITLE_IS_NULL);
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new BusinessException(CaseErrorCode.CASE_TITLE_TOO_LONG);
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new BusinessException(CaseErrorCode.CASE_DESCRIPTION_TOO_LONG);
        }
    }

    private static void validateCategory(CaseCategory category) {
        if (category == null) {
            throw new BusinessException(CaseErrorCode.CASE_CATEGORY_IS_NULL);
        }
    }

    private static void validateDeadline(LocalDate deadline) {
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            throw new BusinessException(CaseErrorCode.CASE_DEADLINE_IS_PAST);
        }
    }

    private static void validateBudget(Long budget) {
        if (budget != null && budget <= 0) {
            throw new BusinessException(CaseErrorCode.CASE_BUDGET_INVALID);
        }
    }


    public void startProgress() {
        if (!this.status.isOpen()) {
            throw new BusinessException(CaseErrorCode.CASE_NOT_OPEN);
        }
        this.status = CaseStatus.IN_PROGRESS;
    }

    public void complete() {
        if (this.status.isTerminated()) {
            throw new BusinessException(CaseErrorCode.CASE_ALREADY_TERMINATED);
        }
        this.status = CaseStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status.isTerminated()) {
            throw new BusinessException(CaseErrorCode.CASE_ALREADY_TERMINATED);
        }
        this.status = CaseStatus.CANCELLED;
    }


    public void updateTitle(String title) {
        validateTitle(title);
        this.title = title;
    }

    public void updateDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    public void updateCategory(CaseCategory category) {
        validateCategory(category);
        this.category = category;
    }

    public void updateDeadline(LocalDate deadline) {
        validateDeadline(deadline);
        this.preferredDeadline = deadline;
    }

    public void updateBudget(Long budget) {
        validateBudget(budget);
        this.budget = budget;
    }

    public void updateToothNumbers(String toothNumbers) {
        this.toothNumbers = toothNumbers;
    }


    public void incrementProposalCount() {
        this.proposalCount++;
    }

    public void decrementProposalCount() {
        if (this.proposalCount > 0) {
            this.proposalCount--;
        }
    }


    public boolean isOwner(Long userId) {
        return this.clientId.equals(userId);
    }

    public boolean isOpen() {
        return this.status.isOpen();
    }
}
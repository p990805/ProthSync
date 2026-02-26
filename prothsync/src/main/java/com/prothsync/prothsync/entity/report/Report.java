package com.prothsync.prothsync.entity.report;

import com.prothsync.prothsync.common.BaseEntity;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.ReportErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "reports",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_reports_reporter_target",
            columnNames = {"reporter_id", "target_type", "target_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

    private static final int MAX_DESCRIPTION_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "target_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportTargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column
    private Long processedBy;

    @Column
    private LocalDateTime processedAt;

    private Report(Long reporterId, ReportTargetType targetType, Long targetId,
        ReportReason reason, String description) {
        this.reporterId = reporterId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.description = description;
        this.status = ReportStatus.PENDING;
    }

    public static Report create(Long reporterId, ReportTargetType targetType, Long targetId,
        ReportReason reason, String description) {
        validateReporterId(reporterId);
        validateTargetType(targetType);
        validateTargetId(targetId);
        validateReason(reason);
        validateDescription(description);

        return new Report(reporterId, targetType, targetId, reason, description);
    }

    public void process(Long adminId, ReportStatus status) {
        validateProcessStatus(status);
        this.processedBy = adminId;
        this.processedAt = LocalDateTime.now();
        this.status = status;
    }

    public boolean isPending() {
        return this.status == ReportStatus.PENDING;
    }

    private static void validateReporterId(Long reporterId) {
        if (reporterId == null) {
            throw new BusinessException(ReportErrorCode.REPORTER_ID_NULL);
        }
    }

    private static void validateTargetType(ReportTargetType targetType) {
        if (targetType == null) {
            throw new BusinessException(ReportErrorCode.TARGET_TYPE_NULL);
        }
    }

    private static void validateTargetId(Long targetId) {
        if (targetId == null) {
            throw new BusinessException(ReportErrorCode.TARGET_ID_NULL);
        }
    }

    private static void validateReason(ReportReason reason) {
        if (reason == null) {
            throw new BusinessException(ReportErrorCode.REASON_NULL);
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new BusinessException(ReportErrorCode.DESCRIPTION_TOO_LONG);
        }
    }

    private static void validateProcessStatus(ReportStatus status) {
        if (status == null || status == ReportStatus.PENDING) {
            throw new BusinessException(ReportErrorCode.INVALID_PROCESS_STATUS);
        }
    }
}
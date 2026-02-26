package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.report.Report;
import com.prothsync.prothsync.entity.report.ReportReason;
import com.prothsync.prothsync.entity.report.ReportStatus;
import com.prothsync.prothsync.entity.report.ReportTargetType;
import java.time.LocalDateTime;

public record ReportResponseDTO(
    Long reportId,
    Long reporterId,
    ReportTargetType targetType,
    Long targetId,
    ReportReason reason,
    String description,
    ReportStatus status,
    Long processedBy,
    LocalDateTime processedAt,
    LocalDateTime createdAt
) {

    public static ReportResponseDTO from(Report report) {
        return new ReportResponseDTO(
            report.getReportId(),
            report.getReporterId(),
            report.getTargetType(),
            report.getTargetId(),
            report.getReason(),
            report.getDescription(),
            report.getStatus(),
            report.getProcessedBy(),
            report.getProcessedAt(),
            report.getCreatedAt()
        );
    }
}
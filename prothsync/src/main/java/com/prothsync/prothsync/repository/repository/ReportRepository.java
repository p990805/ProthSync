package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.report.Report;
import com.prothsync.prothsync.entity.report.ReportStatus;
import com.prothsync.prothsync.entity.report.ReportTargetType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportRepository {

    Report save(Report report);

    Optional<Report> findById(Long reportId);

    boolean existsByReporterIdAndTargetTypeAndTargetId(
        Long reporterId, ReportTargetType targetType, Long targetId);

    Page<Report> findAllByReporterId(Long reporterId, Pageable pageable);

    Page<Report> findAllByStatus(ReportStatus status, Pageable pageable);

    long countByStatus(ReportStatus status);
}
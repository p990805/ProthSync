package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.report.Report;
import com.prothsync.prothsync.entity.report.ReportStatus;
import com.prothsync.prothsync.entity.report.ReportTargetType;
import com.prothsync.prothsync.repository.jpa.ReportJpaRepository;
import com.prothsync.prothsync.repository.repository.ReportRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final ReportJpaRepository reportJpaRepository;

    @Override
    public Report save(Report report) {
        return reportJpaRepository.save(report);
    }

    @Override
    public Optional<Report> findById(Long reportId) {
        return reportJpaRepository.findById(reportId);
    }

    @Override
    public boolean existsByReporterIdAndTargetTypeAndTargetId(
        Long reporterId, ReportTargetType targetType, Long targetId) {
        return reportJpaRepository.existsByReporterIdAndTargetTypeAndTargetId(
            reporterId, targetType, targetId);
    }

    @Override
    public Page<Report> findAllByReporterId(Long reporterId, Pageable pageable) {
        return reportJpaRepository.findAllByReporterId(reporterId, pageable);
    }

    @Override
    public Page<Report> findAllByStatus(ReportStatus status, Pageable pageable) {
        return reportJpaRepository.findAllByStatus(status, pageable);
    }

    @Override
    public long countByStatus(ReportStatus status) {
        return reportJpaRepository.countByStatus(status);
    }
}
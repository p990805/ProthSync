package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.ReportCreateRequestDTO;
import com.prothsync.prothsync.dto.ReportResponseDTO;
import com.prothsync.prothsync.entity.report.Report;
import com.prothsync.prothsync.entity.report.ReportTargetType;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.PostErrorCode;
import com.prothsync.prothsync.exception.ReportErrorCode;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.exception.CaseErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.CaseRequestRepository;
import com.prothsync.prothsync.repository.repository.CommentRepository;
import com.prothsync.prothsync.repository.repository.PostRepository;
import com.prothsync.prothsync.repository.repository.ReportRepository;
import com.prothsync.prothsync.repository.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CaseRequestRepository caseRequestRepository;

    @Transactional
    public ReportResponseDTO createReport(Long reporterId, ReportCreateRequestDTO request) {
        if (request.targetType() == ReportTargetType.USER
            && reporterId.equals(request.targetId())) {
            throw new BusinessException(ReportErrorCode.CANNOT_REPORT_SELF);
        }

        validateTargetExists(request.targetType(), request.targetId());
        validateNotSelfReport(reporterId, request.targetType(), request.targetId());

        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
            reporterId, request.targetType(), request.targetId())) {
            throw new BusinessException(ReportErrorCode.ALREADY_REPORTED);
        }

        Report report = Report.create(
            reporterId,
            request.targetType(),
            request.targetId(),
            request.reason(),
            request.description()
        );

        Report savedReport = reportRepository.save(report);
        return ReportResponseDTO.from(savedReport);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReportResponseDTO> getMyReports(Long reporterId, Pageable pageable) {
        Page<Report> reportPage = reportRepository.findAllByReporterId(reporterId, pageable);

        List<ReportResponseDTO> reports = reportPage.getContent().stream()
            .map(ReportResponseDTO::from)
            .toList();

        return PageResponse.of(reports, reportPage);
    }

    private void validateTargetExists(ReportTargetType targetType, Long targetId) {
        switch (targetType) {
            case USER -> userRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_TARGET_NOT_FOUND));
            case POST -> postRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_TARGET_NOT_FOUND));
            case COMMENT -> commentRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_TARGET_NOT_FOUND));
            case CASE_REQUEST -> caseRequestRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_TARGET_NOT_FOUND));
        }
    }

    private void validateNotSelfReport(Long reporterId, ReportTargetType targetType, Long targetId) {
        Long ownerId = null;

        switch (targetType) {
            case POST -> ownerId = postRepository.findById(targetId)
                .map(post -> post.getUserId())
                .orElse(null);
            case COMMENT -> ownerId = commentRepository.findById(targetId)
                .map(comment -> comment.getUserId())
                .orElse(null);
            case CASE_REQUEST -> ownerId = caseRequestRepository.findById(targetId)
                .map(caseRequest -> caseRequest.getClientId())
                .orElse(null);
            default -> { return; }
        }

        if (reporterId.equals(ownerId)) {
            throw new BusinessException(ReportErrorCode.CANNOT_REPORT_SELF);
        }
    }
}
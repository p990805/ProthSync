package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.AdminDashboardResponseDTO;
import com.prothsync.prothsync.dto.AdminUserResponseDTO;
import com.prothsync.prothsync.dto.ReportProcessRequestDTO;
import com.prothsync.prothsync.dto.ReportResponseDTO;
import com.prothsync.prothsync.dto.UserRoleUpdateRequestDTO;
import com.prothsync.prothsync.dto.UserSuspendRequestDTO;
import com.prothsync.prothsync.entity.post.Comment;
import com.prothsync.prothsync.entity.post.Post;
import com.prothsync.prothsync.entity.caserequest.CaseRequest;
import com.prothsync.prothsync.entity.report.Report;
import com.prothsync.prothsync.entity.report.ReportStatus;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.entity.user.UserRole;
import com.prothsync.prothsync.exception.AdminErrorCode;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.CaseErrorCode;
import com.prothsync.prothsync.exception.PostErrorCode;
import com.prothsync.prothsync.exception.ReportErrorCode;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.BookmarkRepository;
import com.prothsync.prothsync.repository.repository.CaseRequestRepository;
import com.prothsync.prothsync.repository.repository.CommentRepository;
import com.prothsync.prothsync.repository.repository.PostLikeRepository;
import com.prothsync.prothsync.repository.repository.PostRepository;
import com.prothsync.prothsync.repository.repository.RefreshTokenRepository;
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
public class AdminService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CaseRequestRepository caseRequestRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final HashtagService hashtagService;
    private final PostImageService postImageService;
    private final CommentService commentService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public PageResponse<ReportResponseDTO> getReports(ReportStatus status, Pageable pageable) {
        Page<Report> reportPage;

        if (status != null) {
            reportPage = reportRepository.findAllByStatus(status, pageable);
        } else {
            reportPage = reportRepository.findAllByStatus(ReportStatus.PENDING, pageable);
        }

        List<ReportResponseDTO> reports = reportPage.getContent().stream()
            .map(ReportResponseDTO::from)
            .toList();

        return PageResponse.of(reports, reportPage);
    }

    @Transactional(readOnly = true)
    public ReportResponseDTO getReport(Long reportId) {
        Report report = findReportOrThrow(reportId);
        return ReportResponseDTO.from(report);
    }

    @Transactional
    public ReportResponseDTO processReport(Long reportId, Long adminId, ReportProcessRequestDTO request) {
        Report report = findReportOrThrow(reportId);

        if (!report.isPending()) {
            throw new BusinessException(ReportErrorCode.REPORT_ALREADY_PROCESSED);
        }

        report.process(adminId, request.status());
        Report processedReport = reportRepository.save(report);

        return ReportResponseDTO.from(processedReport);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponseDTO> getUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        List<AdminUserResponseDTO> users = userPage.getContent().stream()
            .map(AdminUserResponseDTO::from)
            .toList();

        return PageResponse.of(users, userPage);
    }

    @Transactional
    public AdminUserResponseDTO suspendUser(Long userId, UserSuspendRequestDTO request) {
        User user = findUserOrThrow(userId);

        if (user.isAdmin()) {
            throw new BusinessException(AdminErrorCode.CANNOT_SUSPEND_ADMIN);
        }
        if (user.isSuspended()) {
            throw new BusinessException(AdminErrorCode.USER_ALREADY_SUSPENDED);
        }

        user.suspend(request.reason(), request.suspendUntil());
        userRepository.save(user);

        refreshTokenRepository.deleteByUserId(userId);

        return AdminUserResponseDTO.from(user);
    }

    @Transactional
    public AdminUserResponseDTO unsuspendUser(Long userId) {
        User user = findUserOrThrow(userId);

        if (!user.isSuspended()) {
            throw new BusinessException(AdminErrorCode.USER_NOT_SUSPENDED);
        }

        user.unsuspend();
        userRepository.save(user);

        return AdminUserResponseDTO.from(user);
    }

    @Transactional
    public AdminUserResponseDTO updateUserRole(Long userId, Long adminId, UserRoleUpdateRequestDTO request) {
        if (userId.equals(adminId)) {
            throw new BusinessException(AdminErrorCode.CANNOT_CHANGE_OWN_ROLE);
        }

        User user = findUserOrThrow(userId);

        if (request.role() == UserRole.ADMIN) {
            user.promoteToAdmin();
        } else {
            user.demoteToUser();
        }

        userRepository.save(user);
        return AdminUserResponseDTO.from(user);
    }


    @Transactional
    public void deletePost(Long postId, Long adminUserId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        postLikeRepository.deleteAllByPostId(postId);
        bookmarkRepository.deleteAllByPostId(postId);
        commentService.softDeleteAllByPostId(postId, adminUserId);
        hashtagService.removeAllHashtagsFromPost(postId);
        postImageService.softDeleteAllByPostId(postId, adminUserId);
        postRepository.softDelete(post, adminUserId);
    }

    @Transactional
    public void deleteComment(Long commentId, Long adminUserId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new BusinessException(PostErrorCode.COMMENT_NOT_FOUND));

        Post post = postRepository.findById(comment.getPostId())
            .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        post.decrementCommentCount();
        postRepository.save(post);

        commentRepository.softDelete(comment, adminUserId);
    }

    @Transactional
    public void deleteCaseRequest(Long caseRequestId, Long adminUserId) {
        CaseRequest caseRequest = caseRequestRepository.findById(caseRequestId)
            .orElseThrow(() -> new BusinessException(CaseErrorCode.CASE_REQUEST_NOT_FOUND));

        caseRequestRepository.softDelete(caseRequest, adminUserId);
    }


    @Transactional(readOnly = true)
    public AdminDashboardResponseDTO getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalPosts = postRepository.count();
        long totalCaseRequests = caseRequestRepository.count();
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING);
        long acceptedReports = reportRepository.countByStatus(ReportStatus.ACCEPTED);
        long rejectedReports = reportRepository.countByStatus(ReportStatus.REJECTED);

        return AdminDashboardResponseDTO.of(
            totalUsers, totalPosts, totalCaseRequests,
            pendingReports, acceptedReports, rejectedReports
        );
    }


    private Report findReportOrThrow(Long reportId) {
        return reportRepository.findById(reportId)
            .orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_NOT_FOUND));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}
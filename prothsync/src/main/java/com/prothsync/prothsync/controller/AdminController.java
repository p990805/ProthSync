package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.AdminControllerDocs;
import com.prothsync.prothsync.dto.AdminDashboardResponseDTO;
import com.prothsync.prothsync.dto.AdminUserResponseDTO;
import com.prothsync.prothsync.dto.ReportProcessRequestDTO;
import com.prothsync.prothsync.dto.ReportResponseDTO;
import com.prothsync.prothsync.dto.UserRoleUpdateRequestDTO;
import com.prothsync.prothsync.dto.UserSuspendRequestDTO;
import com.prothsync.prothsync.entity.report.ReportStatus;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController implements AdminControllerDocs {

    private final AdminService adminService;

    @GetMapping("/reports")
    public ResponseEntity<PageResponse<ReportResponseDTO>> getReports(
        @RequestParam(required = false) ReportStatus status,
        Pageable pageable
    ) {
        PageResponse<ReportResponseDTO> response = adminService.getReports(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/{reportId}")
    public ResponseEntity<ReportResponseDTO> getReport(
        @PathVariable Long reportId
    ) {
        ReportResponseDTO response = adminService.getReport(reportId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/reports/{reportId}")
    public ResponseEntity<ReportResponseDTO> processReport(
        @PathVariable Long reportId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody ReportProcessRequestDTO request
    ) {
        ReportResponseDTO response = adminService.processReport(
            reportId, userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/users")
    public ResponseEntity<PageResponse<AdminUserResponseDTO>> getUsers(
        Pageable pageable
    ) {
        PageResponse<AdminUserResponseDTO> response = adminService.getUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/suspend")
    public ResponseEntity<AdminUserResponseDTO> suspendUser(
        @PathVariable Long userId,
        @Valid @RequestBody UserSuspendRequestDTO request
    ) {
        AdminUserResponseDTO response = adminService.suspendUser(userId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/unsuspend")
    public ResponseEntity<AdminUserResponseDTO> unsuspendUser(
        @PathVariable Long userId
    ) {
        AdminUserResponseDTO response = adminService.unsuspendUser(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<AdminUserResponseDTO> updateUserRole(
        @PathVariable Long userId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody UserRoleUpdateRequestDTO request
    ) {
        AdminUserResponseDTO response = adminService.updateUserRole(
            userId, userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
        @PathVariable Long postId
    ) {
        adminService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @PathVariable Long commentId
    ) {
        adminService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cases/{caseRequestId}")
    public ResponseEntity<Void> deleteCaseRequest(
        @PathVariable Long caseRequestId
    ) {
        adminService.deleteCaseRequest(caseRequestId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardResponseDTO> getDashboardStats() {
        AdminDashboardResponseDTO response = adminService.getDashboardStats();
        return ResponseEntity.ok(response);
    }
}
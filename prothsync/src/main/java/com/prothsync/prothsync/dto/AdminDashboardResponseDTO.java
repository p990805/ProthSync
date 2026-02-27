package com.prothsync.prothsync.dto;

public record AdminDashboardResponseDTO(
    long totalUsers,
    long totalPosts,
    long totalCaseRequests,
    long pendingReports,
    long acceptedReports,
    long rejectedReports
) {

    public static AdminDashboardResponseDTO of(
        long totalUsers,
        long totalPosts,
        long totalCaseRequests,
        long pendingReports,
        long acceptedReports,
        long rejectedReports
    ) {
        return new AdminDashboardResponseDTO(
            totalUsers,
            totalPosts,
            totalCaseRequests,
            pendingReports,
            acceptedReports,
            rejectedReports
        );
    }
}
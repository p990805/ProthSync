package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.NotificationControllerDocs;
import com.prothsync.prothsync.dto.NotificationResponseDTO;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponseDTO>> getMyNotifications(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        Pageable pageable
    ) {
        PageResponse<NotificationResponseDTO> response =
            notificationService.getMyNotifications(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        int count = notificationService.getUnreadCount(userDetails.getUserId());
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
        @PathVariable Long notificationId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.markAsRead(notificationId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Integer> markAllAsRead(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        int count = notificationService.markAllAsRead(userDetails.getUserId());
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
        @PathVariable Long notificationId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.deleteNotification(notificationId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}
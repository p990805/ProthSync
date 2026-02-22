package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.NotificationResponseDTO;
import com.prothsync.prothsync.entity.notification.Notification;
import com.prothsync.prothsync.entity.notification.NotificationType;
import com.prothsync.prothsync.entity.notification.ReferenceType;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.NotificationErrorCode;
import com.prothsync.prothsync.global.PageResponse;
import com.prothsync.prothsync.repository.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;


    @Transactional
    public void send(NotificationType type, Long actorId, Long recipientId,
        Long referenceId, ReferenceType referenceType) {
        if (actorId.equals(recipientId)) {
            return;
        }

        String message = type.getMessage();

        Notification notification = Notification.create(
            recipientId, actorId, type, referenceId, referenceType, message);

        notificationRepository.save(notification);
        log.info("알림 생성 - type: {}, actor: {}, recipient: {}", type, actorId, recipientId);
    }


    @Transactional(readOnly = true)
    public PageResponse<NotificationResponseDTO> getMyNotifications(Long userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByRecipientId(userId, pageable);

        List<NotificationResponseDTO> data = page.getContent().stream()
            .map(NotificationResponseDTO::from)
            .toList();

        return PageResponse.of(data, page);
    }


    @Transactional(readOnly = true)
    public int getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByRecipientId(userId);
    }


    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = findNotificationOrThrow(notificationId);
        validateOwner(notification, userId);

        notification.markAsRead();
        notificationRepository.save(notification);
    }


    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsRead(userId);
    }


    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = findNotificationOrThrow(notificationId);
        validateOwner(notification, userId);

        notification.delete(userId);
        notificationRepository.save(notification);
    }

    private Notification findNotificationOrThrow(Long notificationId) {
        return notificationRepository.findById(notificationId)
            .orElseThrow(() -> new BusinessException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }

    private void validateOwner(Notification notification, Long userId) {
        if (!notification.getRecipientId().equals(userId)) {
            throw new BusinessException(NotificationErrorCode.NOTIFICATION_ACCESS_DENIED);
        }
    }
}
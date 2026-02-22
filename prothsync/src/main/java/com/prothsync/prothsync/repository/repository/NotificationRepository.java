package com.prothsync.prothsync.repository.repository;

import com.prothsync.prothsync.entity.notification.Notification;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(Long notificationId);

    Page<Notification> findByRecipientId(Long recipientId, Pageable pageable);

    int countUnreadByRecipientId(Long recipientId);

    int markAllAsRead(Long recipientId);

    void delete(Notification notification);
}
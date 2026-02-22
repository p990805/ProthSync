package com.prothsync.prothsync.repository.impl;

import com.prothsync.prothsync.entity.notification.Notification;
import com.prothsync.prothsync.repository.jpa.NotificationJpaRepository;
import com.prothsync.prothsync.repository.repository.NotificationRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationJpaRepository.save(notification);
    }

    @Override
    public Optional<Notification> findById(Long notificationId) {
        return notificationJpaRepository.findById(notificationId);
    }

    @Override
    public Page<Notification> findByRecipientId(Long recipientId, Pageable pageable) {
        return notificationJpaRepository.findByRecipientIdAndDeletedAtIsNullOrderByCreatedAtDesc(
            recipientId, pageable);
    }

    @Override
    public int countUnreadByRecipientId(Long recipientId) {
        return notificationJpaRepository.countByRecipientIdAndIsReadFalseAndDeletedAtIsNull(recipientId);
    }

    @Override
    public int markAllAsRead(Long recipientId) {
        return notificationJpaRepository.markAllAsRead(recipientId);
    }

    @Override
    public void delete(Notification notification) {
        notificationJpaRepository.delete(notification);
    }
}
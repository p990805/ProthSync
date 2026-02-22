package com.prothsync.prothsync.repository.jpa;

import com.prothsync.prothsync.entity.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientIdAndDeletedAtIsNullOrderByCreatedAtDesc(
        Long recipientId, Pageable pageable);

    int countByRecipientIdAndIsReadFalseAndDeletedAtIsNull(Long recipientId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipientId = :recipientId AND n.isRead = false AND n.deletedAt IS NULL")
    int markAllAsRead(@Param("recipientId") Long recipientId);
}
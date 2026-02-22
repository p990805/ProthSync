package com.prothsync.prothsync.entity.notification;

import com.prothsync.prothsync.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notification_recipient", columnList = "recipient_id, is_read, created_at")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Column(name = "reference_id")
    private Long referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", length = 20)
    private ReferenceType referenceType;

    @Column(nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    private Notification(Long recipientId, Long actorId, NotificationType type,
        Long referenceId, ReferenceType referenceType, String message) {
        this.recipientId = recipientId;
        this.actorId = actorId;
        this.type = type;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.message = message;
        this.isRead = false;
    }

    public static Notification create(Long recipientId, Long actorId, NotificationType type,
        Long referenceId, ReferenceType referenceType, String message) {
        validateRecipientId(recipientId);
        validateActorId(actorId);
        validateType(type);
        validateMessage(message);

        return new Notification(recipientId, actorId, type, referenceId, referenceType, message);
    }

    public void markAsRead() {
        this.isRead = true;
    }

    // === Validation ===

    private static void validateRecipientId(Long recipientId) {
        if (recipientId == null) {
            throw new IllegalArgumentException("알림 수신자 ID는 필수입니다.");
        }
    }

    private static void validateActorId(Long actorId) {
        if (actorId == null) {
            throw new IllegalArgumentException("알림 발생자 ID는 필수입니다.");
        }
    }

    private static void validateType(NotificationType type) {
        if (type == null) {
            throw new IllegalArgumentException("알림 타입은 필수입니다.");
        }
    }

    private static void validateMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("알림 메시지는 필수입니다.");
        }
    }
}
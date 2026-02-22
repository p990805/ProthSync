package com.prothsync.prothsync.dto;

import com.prothsync.prothsync.entity.notification.Notification;
import com.prothsync.prothsync.entity.notification.NotificationType;
import com.prothsync.prothsync.entity.notification.ReferenceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "알림 응답 DTO")
public record NotificationResponseDTO(

    @Schema(description = "알림 ID")
    Long notificationId,

    @Schema(description = "알림 발생자 ID")
    Long actorId,

    @Schema(description = "알림 타입")
    NotificationType type,

    @Schema(description = "참조 엔티티 ID")
    Long referenceId,

    @Schema(description = "참조 엔티티 타입")
    ReferenceType referenceType,

    @Schema(description = "알림 메시지")
    String message,

    @Schema(description = "읽음 여부")
    boolean isRead,

    @Schema(description = "생성일시")
    LocalDateTime createdAt
) {

    public static NotificationResponseDTO from(Notification notification) {
        return new NotificationResponseDTO(
            notification.getNotificationId(),
            notification.getActorId(),
            notification.getType(),
            notification.getReferenceId(),
            notification.getReferenceType(),
            notification.getMessage(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }
}
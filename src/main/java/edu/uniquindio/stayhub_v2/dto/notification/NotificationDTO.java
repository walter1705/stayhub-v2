package edu.uniquindio.stayhub_v2.dto.notification;

import edu.uniquindio.stayhub_v2.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "In-app notification")
public record NotificationDTO(
        @Schema(description = "Notification ID", example = "notif_01HXYZ")
        String id,

        @Schema(description = "Notification type")
        NotificationType type,

        @Schema(description = "Short title", example = "Pago pendiente")
        String title,

        @Schema(description = "Message body", example = "Tenés un depósito pendiente por pagar.")
        String body,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Read timestamp (null if unread)", nullable = true)
        LocalDateTime readAt
) {}

package edu.uniquindio.stayhub_v2.service;

import edu.uniquindio.stayhub_v2.dto.notification.NotificationDTO;
import edu.uniquindio.stayhub_v2.model.Notification;
import edu.uniquindio.stayhub_v2.model.NotificationType;
import edu.uniquindio.stayhub_v2.model.User;
import edu.uniquindio.stayhub_v2.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<NotificationDTO> listNotifications(int page, int size) {
        User user = userService.getCurrentUser();
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size))
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<NotificationDTO> listHostNotifications(int page) {
        User user = userService.getCurrentUser();
        List<NotificationType> hostTypes = List.of(
                NotificationType.BOOKING_CREATED,
                NotificationType.BOOKING_CANCELLED,
                NotificationType.REVIEW_CREATED);
        return notificationRepository
                .findByUserIdAndTypeInOrderByCreatedAtDesc(user.getId(), hostTypes, PageRequest.of(page, 20))
                .map(this::toDTO);
    }

    @Transactional
    public NotificationDTO markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("Notification not found: " + notificationId));

        User user = userService.getCurrentUser();
        if (!notification.getUserId().equals(user.getId())) {
            throw new SecurityException("No tienes permisos para marcar esta notificación.");
        }

        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
        return toDTO(notification);
    }

    public Notification createNotification(Long userId, NotificationType type, String title, String body) {
        Notification notification = Notification.builder()
                .id("notif_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12))
                .userId(userId)
                .type(type)
                .title(title)
                .body(body)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }

    private NotificationDTO toDTO(Notification n) {
        return new NotificationDTO(n.getId(), n.getType(), n.getTitle(), n.getBody(), n.getCreatedAt(), n.getReadAt());
    }
}

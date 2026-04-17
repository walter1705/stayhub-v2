package edu.uniquindio.stayhub_v2.repository;

import edu.uniquindio.stayhub_v2.model.Notification;
import edu.uniquindio.stayhub_v2.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Notification> findByUserIdAndTypeInOrderByCreatedAtDesc(
            Long userId, List<NotificationType> types, Pageable pageable);
}

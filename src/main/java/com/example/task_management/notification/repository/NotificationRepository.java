package com.example.task_management.notification.repository;

import com.example.task_management.common.enums.NotificationStatus;
import com.example.task_management.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStatusAndRetryCountLessThanOrderByCreatedOnDesc(NotificationStatus status, int maxRetries, Pageable pageable);
}

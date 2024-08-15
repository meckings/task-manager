package com.example.task_management.notification.service;

import com.example.task_management.auth.entity.User;
import com.example.task_management.common.enums.NotificationStatus;
import com.example.task_management.notification.client.NotificationClient;
import com.example.task_management.notification.dto.NotificationRequest;
import com.example.task_management.notification.entity.Notification;
import com.example.task_management.notification.repository.NotificationRepository;
import com.example.task_management.task.entity.Task;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationClient<NotificationRequest> notificationClient;

    @Value("${notification.retry.batch-size}")
    private int retryBatchSize;

    @Value("${notification.retry.max-attempts}")
    private int maxRetryAttempts;

    /**
     * In a production environment, I would the notification service as a
     * separate microservice and make other services send requests to it
     * through a queue
     */
    @Transactional
    public void sendNotification(Task task, User user, String subject, String message) {
        NotificationRequest request = new NotificationRequest(user.getEmail(), message, subject);
        NotificationStatus status = send(request);
        Notification notification = new Notification(task, user, message, subject, status, 0);
        notificationRepository.save(notification);
    }

    /**
     * Normally I would do this using a queue
     */
    @Transactional
    @Scheduled(fixedDelayString = "${notification.retry.interval}", timeUnit = TimeUnit.MINUTES)
    public void retryFailedNotifications() {
        notificationRepository.findByStatusAndRetryCountLessThanOrderByCreatedOnDesc(NotificationStatus.FAILED, maxRetryAttempts, Pageable.ofSize(retryBatchSize))
                .forEach(notification -> {
                    log.info("Retrying notification {}", notification);
                    NotificationRequest request = new NotificationRequest(notification.getUser().getEmail(), notification.getMessage(), notification.getSubject());
                    NotificationStatus status = send(request);
                    notification.setStatus(status);
                    notification.incrementRetryCount();
                    notificationRepository.save(notification);
                });
    }

    private NotificationStatus send(NotificationRequest request) {
        NotificationStatus status = NotificationStatus.SENT;
        try {
            notificationClient.sendNotification(request);
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
            status = NotificationStatus.FAILED;
        }
        return status;
    }
}

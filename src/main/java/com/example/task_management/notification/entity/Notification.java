package com.example.task_management.notification.entity;

import com.example.task_management.auth.entity.User;
import com.example.task_management.common.entity.BaseEntity;
import com.example.task_management.common.enums.NotificationStatus;
import com.example.task_management.task.entity.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    public Notification(Task task, User user, String message, String subject, NotificationStatus status) {
        this.task = task;
        this.user = user;
        this.message = message;
        this.subject = subject;
        this.status = status;
        this.retryCount = 0;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }
}


package com.example.task_management.notification.client;

public interface NotificationClient<S> {

    void sendNotification(S message);
}

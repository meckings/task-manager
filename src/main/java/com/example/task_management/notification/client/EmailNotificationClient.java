package com.example.task_management.notification.client;

import com.example.task_management.notification.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationClient implements NotificationClient<NotificationRequest> {

    private final JavaMailSender javaMailSender;
    @Value("${notification.email}")
    private String email;

    public void sendSimpleMessage(NotificationRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getRecipientEmail());
        message.setFrom(email);
        message.setSubject(request.getSubject());
        message.setText(request.getMessage());
        javaMailSender.send(message);
    }

    @Override
    public void sendNotification(NotificationRequest request) {
        sendSimpleMessage(request);
    }
}

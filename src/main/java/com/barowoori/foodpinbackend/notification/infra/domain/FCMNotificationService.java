package com.barowoori.foodpinbackend.notification.infra.domain;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationTargetType;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationType;
import com.barowoori.foodpinbackend.notification.command.domain.service.NotificationService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class FCMNotificationService implements NotificationService {

    public FCMNotificationService() {
    }

    @PostConstruct
    public void init() {
        try {
            String FCM_PRIVATE_KEY_PATH = "/secrets/foodpin-d001c-firebase-adminsdk-idqc0-9e68a6effd.json";
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(
                            GoogleCredentials
                                    .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
                                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform")))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase 연결 완료");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void pushAlarmToToken(NotificationType type,  String title, String content, String token, NotificationTargetType targetType, String targetId) {
        if (token == null || token.isEmpty()) {
            return;
        }
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build();
        Message.Builder builder = Message.builder();
        Message message = builder
                .setToken(token)
                .setNotification(notification)
                .putData("type", type.name())
                .putData("targetType", targetType.toString())
                .putData("targetId", targetId)
                .build();
        sendMessage(message);
        log.info("fcm content : {}", content);
    }

    public void sendMessage(Message message) {
        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();

            log.info("전송 성공 {}", response);
        } catch (ExecutionException | InterruptedException e) {
            log.info("실패 {}", e.getMessage());
        }
    }
}

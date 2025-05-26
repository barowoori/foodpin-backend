package com.barowoori.foodpinbackend.event.command.application.service;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.ReplyRequestNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPushNotificator {

    @Scheduled(cron = "0 0 * * * *")
    public void sendRepeatedReplyRequestPushNotification() {
        NotificationEvent.raise(new ReplyRequestNotificationEvent());
    }


}

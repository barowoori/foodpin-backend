package com.barowoori.foodpinbackend.event.command.application.service;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.InterestDeadlineSoonNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.RecruitmentDeadlineSoonNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.ReplyRequestNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.SelectionEndedNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class EventPushNotificator {

    @Scheduled(cron = "0 0 * * * *")
    public void sendRepeatedReplyRequestPushNotification() {
        NotificationEvent.raise(new ReplyRequestNotificationEvent());
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void sendSelectionEndPushNotification() {
        NotificationEvent.raise(new SelectionEndedNotificationEvent());
    }

    @Scheduled(cron = "0 * * * * *")
    public void sendRecruitmentDeadlineSoonPushNotification() {
        log.info("모집마감 알림 전송 시작");
        NotificationEvent.raise(new RecruitmentDeadlineSoonNotificationEvent());
        NotificationEvent.raise(new InterestDeadlineSoonNotificationEvent());
        log.info("모집마감 알림 전송 종료");
    }

}

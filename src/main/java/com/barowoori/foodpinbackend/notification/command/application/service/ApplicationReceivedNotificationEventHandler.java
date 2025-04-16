package com.barowoori.foodpinbackend.notification.command.application.service;

import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.notification.command.domain.model.ApplicationReceivedNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationTargetType;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationType;
import com.barowoori.foodpinbackend.notification.command.domain.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApplicationReceivedNotificationEventHandler {
    private final NotificationService notificationService;
    private final EventRepository eventRepository;

    public ApplicationReceivedNotificationEventHandler(NotificationService notificationService, EventRepository eventRepository) {
        this.notificationService = notificationService;
        this.eventRepository = eventRepository;

    }

    @EventListener(ApplicationReceivedNotificationEvent.class)
    public void handle(ApplicationReceivedNotificationEvent event) {
        NotificationType type = NotificationType.APPLICATION_RECEIVED;
        NotificationTargetType targetType = NotificationTargetType.EVENT_APPLICATION_DETAIL;

        String content = type.format(Map.of(
                "행사명", event.getEventName()
        ));
        System.out.println("==== "+ content);
        String eventCreatorToken = eventRepository.findEventCreatorFcmToken(event.getEventId());
        notificationService.pushAlarmToToken(type, targetType.name(), content, eventCreatorToken, targetType, event.getEventApplicationId());
    }
}

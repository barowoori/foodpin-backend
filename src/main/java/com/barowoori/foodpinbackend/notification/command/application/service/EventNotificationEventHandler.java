package com.barowoori.foodpinbackend.notification.command.application.service;

import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.notification.command.domain.model.*;
import com.barowoori.foodpinbackend.notification.command.domain.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventNotificationEventHandler {
    private final NotificationService notificationService;
    private final EventRepository eventRepository;

    public EventNotificationEventHandler(NotificationService notificationService, EventRepository eventRepository) {
        this.notificationService = notificationService;
        this.eventRepository = eventRepository;

    }

    //지원자 알림 handler
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

    //선정 확정 알림 handler
    @EventListener(SelectionConfirmedNotificationEvent.class)
    public void handle(SelectionConfirmedNotificationEvent event) {
        NotificationType type = NotificationType.SELECTION_CONFIRMED;
        NotificationTargetType targetType = NotificationTargetType.EVENT_APPLICATION_SELECTED_LIST;

        String content = type.format(Map.of(
                "행사명", event.getEventName(),
                "푸드트럭명", event.getTruckName()
        ));
        System.out.println("==== "+ content);
        String eventCreatorToken = eventRepository.findEventCreatorFcmToken(event.getEventId());
        notificationService.pushAlarmToToken(type, targetType.name(), content, eventCreatorToken, targetType, event.getEventTruckId());
    }

    //선정 취소 알림 handler
    @EventListener(SelectionCanceledNotificationEvent.class)
    public void handle(SelectionCanceledNotificationEvent event) {
        NotificationType type = NotificationType.SELECTION_CANCELED;
        NotificationTargetType targetType = NotificationTargetType.EVENT_APPLICATION_LIST;

        String content = type.format(Map.of(
                "행사명", event.getEventName(),
                "푸드트럭명", event.getTruckName()
        ));
        System.out.println("notificationMessage : "+ content);
        String eventCreatorToken = eventRepository.findEventCreatorFcmToken(event.getEventId());
        notificationService.pushAlarmToToken(type, targetType.name(), content, eventCreatorToken, targetType, null);
    }
}

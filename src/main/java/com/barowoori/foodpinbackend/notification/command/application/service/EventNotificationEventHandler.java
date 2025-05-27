package com.barowoori.foodpinbackend.notification.command.application.service;

import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.MemberForEventFcmInfoDto;
import com.barowoori.foodpinbackend.notification.command.domain.model.*;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.*;
import com.barowoori.foodpinbackend.notification.command.domain.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EventNotificationEventHandler {
    private final NotificationService notificationService;
    private final EventRepository eventRepository;
    private final EventTruckRepository eventTruckRepository;

    public EventNotificationEventHandler(NotificationService notificationService, EventRepository eventRepository, EventTruckRepository eventTruckRepository) {
        this.notificationService = notificationService;
        this.eventRepository = eventRepository;
        this.eventTruckRepository = eventTruckRepository;

    }

    //지원자 알림 handler
    @EventListener(ApplicationReceivedNotificationEvent.class)
    public void handle(ApplicationReceivedNotificationEvent event) {
        NotificationType type = NotificationType.APPLICATION_RECEIVED;
        NotificationTargetType targetType = NotificationTargetType.EVENT_APPLICATION_DETAIL;

        String content = type.format(Map.of(
                "행사명", event.getEventName()
        ));
        System.out.println("notificationMessage : " + content);
        MemberFcmInfoDto eventCreatorFcmInfo = eventRepository.findEventCreatorFcmInfo(event.getEventId());
        if (eventCreatorFcmInfo == null) {
            return;
        }
        notificationService.pushAlarmToToken(type, targetType.name(), content, eventCreatorFcmInfo.getFcmToken(), targetType, event.getEventApplicationId());
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
        System.out.println("notificationMessage : " + content);
        MemberFcmInfoDto eventCreatorFcmInfo = eventRepository.findEventCreatorFcmInfo(event.getEventId());
        if (eventCreatorFcmInfo == null) {
            return;
        }
        notificationService.pushAlarmToToken(type, targetType.name(), content, eventCreatorFcmInfo.getFcmToken(), targetType, event.getEventTruckId());
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
        System.out.println("notificationMessage : " + content);
        MemberFcmInfoDto eventCreatorFcmInfo = eventRepository.findEventCreatorFcmInfo(event.getEventId());
        if (eventCreatorFcmInfo == null) {
            return;
        }
        notificationService.pushAlarmToToken(type, targetType.name(), content, eventCreatorFcmInfo.getFcmToken(), targetType, null);
    }

    //회신 요청 알림 handler
    @EventListener(ReplyRequestNotificationEvent.class)
    public void handle(ReplyRequestNotificationEvent event) {
        NotificationType type = NotificationType.REPLY_REQUEST;
        NotificationTargetType targetType = NotificationTargetType.TRUCK_SELECTED_EVENT_LIST;


        List<MemberForEventFcmInfoDto>  memberFcmInfoDtos = eventTruckRepository.findPendingEventTruckManagersFcmInfo();
        memberFcmInfoDtos.forEach(memberFcmInfoDto -> {
            String content = type.format(Map.of(
                    "행사명", memberFcmInfoDto.getEventName()
            ));
            System.out.println("notificationMessage : " + content);

            notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, memberFcmInfoDto.getEventId());
        });
    }

    //선정종료 알림 handler
    @EventListener(SelectionEndedNotificationEvent.class)
    public void handle(SelectionEndedNotificationEvent event) {
        NotificationType type = NotificationType.SELECTION_ENDED;
        NotificationTargetType targetType = NotificationTargetType.EVENT_MANAGEMENT_LIST;

        List<MemberForEventFcmInfoDto>  memberFcmInfoDtos = eventRepository.findSelectionNotEndedEventCreatorsFcmInfo();

        memberFcmInfoDtos.forEach(memberFcmInfoDto -> {
            String content = type.getTemplate();
            System.out.println("notificationMessage : " + content);

            notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, memberFcmInfoDto.getEventId());
        });
    }


    //모집마감일 알림 handler
    @EventListener(RecruitmentDeadlineSoonNotificationEvent.class)
    public void handle(RecruitmentDeadlineSoonNotificationEvent event) {
        NotificationType type = NotificationType.RECRUITMENT_DEADLINE_SOON;
        NotificationTargetType targetType = NotificationTargetType.EVENT_DETAIL;

        List<MemberForEventFcmInfoDto>  memberFcmInfoDtos = eventRepository.findRecruitmentDeadlineSoonEventCreatorsFcmInfo();

        memberFcmInfoDtos.forEach(memberFcmInfoDto -> {
            String content = type.format(Map.of(
                    "행사명", memberFcmInfoDto.getEventName()
            ));
            System.out.println("notificationMessage : " + content);
            notificationService.pushAlarmToToken(type, targetType.name(), content, memberFcmInfoDto.getFcmToken(), targetType, memberFcmInfoDto.getEventId());
        });
    }
}

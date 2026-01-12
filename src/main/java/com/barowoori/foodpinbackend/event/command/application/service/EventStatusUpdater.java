package com.barowoori.foodpinbackend.event.command.application.service;

import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckDateRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.SelectionCanceledNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.truck.SelectionNotSelectedNotificationEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventStatusUpdater {
    private final EventRepository eventRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final EventTruckRepository eventTruckRepository;
    private final EventTruckDateRepository eventTruckDateRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateEventStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> recruitingEvents = eventRepository
                .findByRecruitDetail_RecruitingStatusAndIsDeletedFalse(EventRecruitingStatus.RECRUITING);

        for (Event event : recruitingEvents) {
            EventRecruitDetail detail = event.getRecruitDetail();
            LocalDateTime recruitEndDateTime = detail.getRecruitEndDateTime();
            LocalDateTime eventEndDateTime = event.getEventDates().stream()
                    .map(eventDate -> LocalDateTime.of(eventDate.getDate(), eventDate.getEndTime()))
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            boolean isEventEnded = eventEndDateTime != null && !eventEndDateTime.toLocalDate().atTime(23, 59, 59).isAfter(now);
            boolean isRecruitEndReached = recruitEndDateTime != null && !recruitEndDateTime.isAfter(now);

            if (isEventEnded && Boolean.TRUE.equals(detail.getIsSelecting())) {
                closeRecruitingAndSelection(event);
            } else if (isRecruitEndReached) {
                closeRecruitingByDeadline(event);
            }
        }
    }

    private void closeRecruitingAndSelection(Event event) {
        EventRecruitDetail detail = event.getRecruitDetail();
        detail.closeSelection();
        detail.updateStatus(EventRecruitingStatus.RECRUITMENT_CLOSED);

        List<EventApplication> pendingApplications =
                eventApplicationRepository.findAllByEventAndStatus(event, EventApplicationStatus.PENDING);

        for (EventApplication application : pendingApplications) {
            application.reject();
            eventApplicationRepository.save(application);

            NotificationEvent.raise(new SelectionNotSelectedNotificationEvent(
                    event.getId(),
                    event.getName(),
                    application.getId()
            ));
        }

        List<EventTruck> eventTrucks = eventTruckRepository.findAllByEventAndStatus(event, EventTruckStatus.PENDING);
        EventRecruitDetail eventRecruitDetail = event.getRecruitDetail();

        for (EventTruck eventTruck : eventTrucks) {
            eventRecruitDetail.decreaseSelectedCount();
            eventTruck.reject();

            NotificationEvent.raise(new SelectionCanceledNotificationEvent(
                    event.getId(),
                    event.getName(),
                    eventTruck.getTruck().getName()
            ));
        }
    }

    private void closeRecruitingByDeadline(Event event) {
        event.updateStatus(EventRecruitingStatus.RECRUITMENT_CLOSED);

        List<EventApplication> pendingApplications =
                eventApplicationRepository.findAllByEventAndStatus(event, EventApplicationStatus.PENDING);

        for (EventApplication application : pendingApplications) {
            application.reject();
            eventApplicationRepository.save(application);

            NotificationEvent.raise(new SelectionNotSelectedNotificationEvent(
                    event.getId(),
                    event.getName(),
                    application.getId()
            ));
        }
    }
}

package com.barowoori.foodpinbackend.event.command.application.service;

import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
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
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void closeRecruitingEventsByDeadline() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> eventsToClose = eventRepository
                .findByRecruitDetail_RecruitingStatusAndRecruitDetail_RecruitEndDateTimeLessThanEqual(
                        EventRecruitingStatus.RECRUITING, now);
        for (Event event : eventsToClose) {
            event.updateStatus(EventRecruitingStatus.RECRUITMENT_CLOSED);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void closeSelectingEventsByEndDate() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> expiredEvents = eventRepository.findEventsEndedAndStillSelecting(now);

        for (Event event : expiredEvents) {
            EventRecruitDetail detail = event.getRecruitDetail();
            detail.closeSelection();

            if (detail.getRecruitingStatus() == EventRecruitingStatus.RECRUITING) {
                detail.updateStatus(EventRecruitingStatus.RECRUITMENT_CLOSED);
            }

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
}
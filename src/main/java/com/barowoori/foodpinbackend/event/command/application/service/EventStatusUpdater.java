package com.barowoori.foodpinbackend.event.command.application.service;

import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventStatusUpdater {
    private final EventRepository eventRepository;
    //TODO 경모 확인 필요
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateInProgressAndCompleted(){
//        LocalDate today = LocalDate.now();
//        List<Event> eventList = eventRepository.findByStatusIn(List.of(
//                EventStatus.SELECTING,
//                EventStatus.IN_PROGRESS
//        ));
//
//        for (Event event : eventList){
//            if (event.getEventDates().isEmpty())
//                continue;
//
//            LocalDate minDate = event.getEventDates().stream()
//                    .map(EventDate::getDate)
//                    .min(LocalDate::compareTo)
//                    .orElse(null);
//
//            LocalDate maxDate = event.getEventDates().stream()
//                    .map(EventDate::getDate)
//                    .max(LocalDate::compareTo)
//                    .orElse(null);

//            if (event.getStatus() != EventStatus.IN_PROGRESS && minDate.equals(today)) {
//                event.updateStatus(EventStatus.IN_PROGRESS);
//            }
//
//            if (event.getStatus() != EventStatus.COMPLETED && maxDate.equals(today.minusDays(1))) {
//                event.updateStatus(EventStatus.COMPLETED);
//            }
//        }
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void updateSelecting() {
//        LocalDateTime now = LocalDateTime.now();
//        List<Event> eventsToUpdate = eventRepository.findByStatusAndRecruitDetail_RecruitEndDateTimeLessThanEqual(
//                EventStatus.RECRUITING, now
//        );
//        for (Event event : eventsToUpdate) {
//            event.updateStatus(EventStatus.SELECTING);
//        }
    }
}
package com.barowoori.foodpinbackend.event.command.application.service;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventStatusUpdater {
    private final EventRepository eventRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateInProgressAndCompleted(){
        LocalDate today = LocalDate.now();
        List<Event> eventList = eventRepository.findAll();

        for (Event event : eventList){
            event.updateStatusByDate(today);
        }
    }
}
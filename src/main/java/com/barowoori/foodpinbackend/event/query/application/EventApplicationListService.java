package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventApplicationList;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class EventApplicationListService {
    private final EventApplicationRepository eventApplicationRepository;
    private final EventTruckRepository eventTruckRepository;
    private final ImageManager imageManager;

    public EventApplicationListService(EventApplicationRepository eventApplicationRepository, EventTruckRepository eventTruckRepository, ImageManager imageManager) {
        this.eventApplicationRepository = eventApplicationRepository;
        this.eventTruckRepository = eventTruckRepository;
        this.imageManager = imageManager;
    }

    public Page<EventApplicationList.EventPendingApplication> findPendingEventApplications(String eventId, Pageable pageable) {
        return eventApplicationRepository.findPendingEventApplications(eventId, pageable)
                .map(eventApplication -> EventApplicationList.EventPendingApplication.of(eventApplication, imageManager));
    }

    public Page<EventApplicationList.EventSelectedApplication> findSelectedEventApplications(String eventId, String status, Pageable pageable) {
        return eventTruckRepository.findSelectedEventTrucks(eventId, status, pageable)
                .map(eventTruck -> EventApplicationList.EventSelectedApplication.of(eventTruck, imageManager));
    }

    public Page<EventApplicationList.EventRejectedApplication> findRejectedEventApplications(String eventId, Pageable pageable) {
        return eventApplicationRepository.findRejectedEventApplications(eventId, pageable)
                .map(eventApplication -> EventApplicationList.EventRejectedApplication.of(eventApplication, imageManager));
    }

}

package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.TruckEventApplicationList;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TruckEventApplicationListService {
    private final EventApplicationRepository eventApplicationRepository;
    private final ImageManager imageManager;
    private final EventRegionFullNameGenerator eventRegionFullNameGenerator;
    private final EventTruckRepository eventTruckRepository;

    public TruckEventApplicationListService(EventApplicationRepository eventApplicationRepository,
                                            ImageManager imageManager,
                                            EventRegionFullNameGenerator eventRegionFullNameGenerator,
                                            EventTruckRepository eventTruckRepository) {
        this.eventApplicationRepository = eventApplicationRepository;
        this.imageManager = imageManager;
        this.eventRegionFullNameGenerator = eventRegionFullNameGenerator;
        this.eventTruckRepository = eventTruckRepository;
    }

    public Page<TruckEventApplicationList.AppliedInfo> getTruckEventAppliedApplicationList(String status, String truckId, Pageable pageable) {
        Page<EventApplication> eventApplications = eventApplicationRepository.findAppliedApplications(status, truckId, pageable);
        List<String> eventIds = eventApplications.map(EventApplication::getEvent).map(Event::getId).stream().toList();
        Map<String, List<String>> regionNames = eventRegionFullNameGenerator.findRegionNamesByEventIds(eventIds);
        return eventApplications.map(eventApplication -> TruckEventApplicationList.AppliedInfo.of(eventApplication, convertEventApplicationStatus(eventApplication), regionNames.get(eventApplication.getEvent().getId()), imageManager));
    }

    private String convertEventApplicationStatus(EventApplication eventApplication) {
        if (eventApplication.getStatus().equals(EventApplicationStatus.SELECTED)) {
            return EventApplicationStatus.SELECTED.toString();
        }
        if (eventApplication.getStatus().equals(EventApplicationStatus.REJECTED)) {
            return EventApplicationStatus.REJECTED.toString();
        }
        if (eventApplication.getEvent().getStatus().equals(EventStatus.SELECTING)
                || eventApplication.getEvent().getStatus().equals(EventStatus.IN_PROGRESS)) {
            return EventStatus.RECRUITMENT_CLOSED.toString();
        }
        return eventApplication.getEvent().getStatus().toString();
    }

    public Page<TruckEventApplicationList.SelectedInfo> getTruckEventSelectedApplicationList(String status, String truckId, Pageable pageable) {
        Page<EventTruck> eventTrucks = eventTruckRepository.findSelectedApplications(status, truckId, pageable);
        List<String> eventIds = eventTrucks.map(EventTruck::getEvent).map(Event::getId).stream().toList();
        Map<String, List<String>> regionNames = eventRegionFullNameGenerator.findRegionNamesByEventIds(eventIds);
        return eventTrucks.map(eventTruck -> TruckEventApplicationList.SelectedInfo.of(eventTruck, convertEventTruckStatus(eventTruck), regionNames.get(eventTruck.getEvent().getId()), imageManager));
    }

    private String convertEventTruckStatus(EventTruck eventTruck) {
        if (eventTruck.getStatus().equals(EventTruckStatus.PENDING)) {
            return EventTruckStatus.PENDING.toString();
        }

        if (eventTruck.getStatus().equals(EventTruckStatus.REJECTED)) {
            return EventTruckStatus.REJECTED.toString();
        }

        if (eventTruck.getStatus().equals(EventTruckStatus.CONFIRMED) && eventTruck.getEvent().getStatus().equals(EventStatus.COMPLETED)) {
            return EventStatus.COMPLETED.toString();
        }
        return EventTruckStatus.CONFIRMED.toString();
    }
}

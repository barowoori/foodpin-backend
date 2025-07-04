package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import com.barowoori.foodpinbackend.event.command.domain.model.EventDocument;
import com.barowoori.foodpinbackend.event.command.domain.model.EventTruck;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventDocumentRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventApplicableTruckList;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventApplicableTruckListService {
    private final EventDocumentRepository eventDocumentRepository;
    private final TruckRepository truckRepository;
    private final EventApplicationRepository eventApplicationRepository;

    public EventApplicableTruckListService(EventDocumentRepository eventDocumentRepository, TruckRepository truckRepository, EventApplicationRepository eventApplicationRepository) {
        this.eventDocumentRepository = eventDocumentRepository;
        this.truckRepository = truckRepository;
        this.eventApplicationRepository = eventApplicationRepository;
    }

    @Transactional(readOnly = true)
    public Page<EventApplicableTruckList> findApplicableTrucks(String eventId, String memberId, Pageable pageable) {
        Page<Truck> trucks = truckRepository.findApplicableTrucks(memberId, pageable);
        List<EventApplication> eventApplications = eventApplicationRepository.findEventApplicationsByMemberAndEvent(memberId, eventId);
        if (trucks.getTotalElements() == 0) {
            return trucks.map(truck -> EventApplicableTruckList.of(truck,eventApplications, new ArrayList<>()));
        }
        List<DocumentType> eventDocuments = eventDocumentRepository.findByEventId(eventId).stream().map(EventDocument::getType).toList();
        return trucks.map(truck -> EventApplicableTruckList.of(truck,eventApplications, findMissingDocuments(eventDocuments, truck.getDocuments().stream().toList())));
    }

    private List<DocumentType> findMissingDocuments(List<DocumentType> eventDocuments, List<TruckDocument> truckDocuments) {
        Set<DocumentType> truckDocumentTypes = truckDocuments.stream()
                .map(TruckDocument::getType)
                .collect(Collectors.toSet());
        return eventDocuments.stream()
                .filter(doc -> !truckDocumentTypes.contains(doc))
                .collect(Collectors.toList());
    }
}

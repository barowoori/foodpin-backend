package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import com.barowoori.foodpinbackend.event.command.domain.model.EventTruck;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@Getter
@Builder
public class EventApplicableTruckList {
    private String id;
    private String name;
    private Boolean isApplied;
    private List<String> menuNames;
    private List<DocumentType> missingDocuments;

    public static EventApplicableTruckList of(Truck truck, List<EventApplication> eventApplications, List<DocumentType> missingDocuments) {
        return EventApplicableTruckList.builder()
                .id(truck.getId())
                .name(truck.getName())
                .isApplied(eventApplications.stream().map(EventApplication::getTruck).anyMatch(t-> t.equals(truck)))
                .menuNames(truck.getSortedTruckMenuNames())
                .missingDocuments(missingDocuments)
                .build();
    }
}

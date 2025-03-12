package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
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
    private List<String> menuNames;
    private List<DocumentType> missingDocuments;

    public static EventApplicableTruckList of(Truck truck, List<DocumentType> missingDocuments){
        return EventApplicableTruckList.builder()
                .id(truck.getId())
                .name(truck.getName())
                .menuNames(truck.getMenus().stream()
                        .sorted(Comparator.comparing(TruckMenu::getCreateAt))
                        .map(TruckMenu::getName)
                        .toList())
                .missingDocuments(missingDocuments)
                .build();
    }
}

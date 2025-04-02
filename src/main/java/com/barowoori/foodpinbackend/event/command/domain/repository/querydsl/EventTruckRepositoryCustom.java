package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.event.command.domain.model.EventTruck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventTruckRepositoryCustom {
    Page<EventTruck> findSelectedEventTrucks(String eventId, String status, Pageable pageable);
    Page<EventTruck> findSelectedApplications(String status, String truckId, Pageable pageable);
    Boolean isConfirmedEventTruck(String eventId, String truckId);
    EventTruck findConfirmedEventTruck(String eventId, String truckId);

}

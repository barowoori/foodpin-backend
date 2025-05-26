package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.event.command.domain.model.EventTruck;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventTruckManagerFcmInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventTruckRepositoryCustom {
    Page<EventTruck> findSelectedEventTrucks(String eventId, String status, Pageable pageable);
    Page<EventTruck> findSelectedApplications(String status, String truckId, Pageable pageable);
    Boolean isConfirmedEventTruck(String eventId, String truckId);
    EventTruck findConfirmedEventTruck(String eventId, String truckId);
    List<MemberFcmInfoDto> findEventTruckManagersFcmInfo(String eventTruckId);
    List<MemberFcmInfoDto> findConfirmedEventTruckManagersFcmInfo(String eventId);
    List<EventTruckManagerFcmInfoDto>  findPendingEventTruckManagersFcmInfo();
}

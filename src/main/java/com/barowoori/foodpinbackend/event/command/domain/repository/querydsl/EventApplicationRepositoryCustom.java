package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventApplicationRepositoryCustom {
    Page<EventApplication> findPendingEventApplications(String eventId, Pageable pageable);
    Page<EventApplication> findRejectedEventApplications(String eventId, Pageable pageable);
    Page<EventApplication> findAppliedApplications(String status, String truckId, Pageable pageable);
    List<MemberFcmInfoDto> findAllFcmInfoOfTruckManagersByEventId(String eventId);
    List<MemberFcmInfoDto> findFcmInfoOfTruckManagers(String eventApplicationId);

}

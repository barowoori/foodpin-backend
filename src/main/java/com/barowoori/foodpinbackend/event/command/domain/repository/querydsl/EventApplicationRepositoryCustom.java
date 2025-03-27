package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventApplicationRepositoryCustom {
    Page<EventApplication> findPendingEventApplications(String eventId, Pageable pageable);
    Page<EventApplication> findRejectedEventApplications(String eventId, Pageable pageable);
    Page<EventApplication> findAppliedApplications(String status, String truckId, Pageable pageable);

}

package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplicationStatus;
import com.barowoori.foodpinbackend.event.command.domain.repository.querydsl.EventApplicationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventApplicationRepository extends JpaRepository<EventApplication, String>, EventApplicationRepositoryCustom {
    EventApplication findByTruckIdAndEventId(String truckId, String EventId);
    List<EventApplication> findAllByEventAndStatus(Event event, EventApplicationStatus status);
    List<EventApplication> findAllByTruckId(String truckId);
}

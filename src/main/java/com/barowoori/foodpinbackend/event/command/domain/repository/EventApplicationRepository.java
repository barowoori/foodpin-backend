package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import com.barowoori.foodpinbackend.event.command.domain.repository.querydsl.EventApplicationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventApplicationRepository extends JpaRepository<EventApplication, String>, EventApplicationRepositoryCustom {
    EventApplication findByTruckIdAndEventId(String truckId, String EventId);
}

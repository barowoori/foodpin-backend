package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegionRepository extends JpaRepository<EventRegion, String> {
    EventRegion findByEvent(Event event);
}

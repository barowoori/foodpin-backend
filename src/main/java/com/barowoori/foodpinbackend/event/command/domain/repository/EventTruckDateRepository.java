package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventTruckDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTruckDateRepository extends JpaRepository<EventTruckDate, String> {
}

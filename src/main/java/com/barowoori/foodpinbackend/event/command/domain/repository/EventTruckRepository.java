package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventTruck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTruckRepository extends JpaRepository<EventTruck, String> {
}

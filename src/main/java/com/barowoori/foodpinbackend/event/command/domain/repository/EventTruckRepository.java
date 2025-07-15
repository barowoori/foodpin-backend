package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventTruck;
import com.barowoori.foodpinbackend.event.command.domain.repository.querydsl.EventTruckRepositoryCustom;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventTruckRepository extends JpaRepository<EventTruck, String>, EventTruckRepositoryCustom {
    EventTruck findByEventAndTruck(Event event, Truck truck);
    List<EventTruck> findAllByTruck(Truck truck);
}

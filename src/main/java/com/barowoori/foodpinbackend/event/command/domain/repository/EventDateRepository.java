package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDateRepository extends JpaRepository<EventDate, String> {
}

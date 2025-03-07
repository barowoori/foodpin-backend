package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventApplicationDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventApplicationDateRepository extends JpaRepository<EventApplicationDate, String> {
}

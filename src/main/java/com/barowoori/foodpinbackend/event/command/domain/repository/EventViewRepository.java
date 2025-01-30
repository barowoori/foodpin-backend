package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventViewRepository extends JpaRepository<EventView, String> {
}

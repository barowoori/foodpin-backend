package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.repository.querydsl.EventRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, String>, EventRepositoryCustom {
}

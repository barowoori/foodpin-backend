package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDetailRepository extends JpaRepository<EventDetail, String> {
}

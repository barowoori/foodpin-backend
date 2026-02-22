package com.barowoori.foodpinbackend.truck.command.domain.model;

import com.barowoori.foodpinbackend.event.command.domain.model.EventContactAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventContactAccessLogRepository extends JpaRepository<EventContactAccessLog, String> {
}

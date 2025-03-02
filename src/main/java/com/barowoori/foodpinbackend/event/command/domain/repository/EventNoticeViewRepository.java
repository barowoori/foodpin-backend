package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventNoticeView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventNoticeViewRepository extends JpaRepository<EventNoticeView, String> {
}

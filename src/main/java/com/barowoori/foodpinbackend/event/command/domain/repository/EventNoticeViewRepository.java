package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventNotice;
import com.barowoori.foodpinbackend.event.command.domain.model.EventNoticeView;
import com.barowoori.foodpinbackend.event.command.domain.model.EventTruck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventNoticeViewRepository extends JpaRepository<EventNoticeView, String> {
    EventNoticeView findByEventNoticeAndEventTruck(EventNotice eventNotice, EventTruck eventTruck);
}

package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRecruitDetailRepository extends JpaRepository<EventRecruitDetail, String> {
    EventRecruitDetail findByEvent(Event event);
}

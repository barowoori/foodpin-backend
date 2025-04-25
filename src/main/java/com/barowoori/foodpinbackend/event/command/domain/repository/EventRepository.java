package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventStatus;
import com.barowoori.foodpinbackend.event.command.domain.repository.querydsl.EventRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, String>, EventRepositoryCustom {
    List<Event> findByStatusIn(List<EventStatus> statuses);
    List<Event> findByStatusAndRecruitDetail_RecruitEndDateTimeLessThanEqual(EventStatus status, LocalDateTime time);
}

package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitingStatus;
import com.barowoori.foodpinbackend.event.command.domain.repository.querydsl.EventRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, String>, EventRepositoryCustom {
    List<Event> findByRecruitDetail_RecruitingStatusIn(List<EventRecruitingStatus> statuses);
    List<Event> findByRecruitDetail_RecruitingStatusAndRecruitDetail_RecruitEndDateTimeLessThanEqual(EventRecruitingStatus recruitingStatus, LocalDateTime time);
    List<Event> findAllByCreatedBy(String createdBy);
}

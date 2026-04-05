package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRecruitDetailRepository extends JpaRepository<EventRecruitDetail, String> {
    EventRecruitDetail findByEvent(Event event);

    @Modifying
    @Query("update EventRecruitDetail e set e.applicantCount = e.applicantCount + 1 where e.id = :id")
    void incrementApplicantCount(@Param("id") String id);
}

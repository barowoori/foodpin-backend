package com.barowoori.foodpinbackend.member.command.domain.repository;

import com.barowoori.foodpinbackend.member.command.domain.model.InterestEvent;
import com.barowoori.foodpinbackend.member.command.domain.model.InterestEventRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestEventRegionRepository extends JpaRepository<InterestEventRegion, String> {
    void deleteAllByInterestEvent(InterestEvent interestEvent);
}

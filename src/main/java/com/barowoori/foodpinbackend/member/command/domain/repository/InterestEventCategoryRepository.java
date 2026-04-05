package com.barowoori.foodpinbackend.member.command.domain.repository;

import com.barowoori.foodpinbackend.member.command.domain.model.InterestEvent;
import com.barowoori.foodpinbackend.member.command.domain.model.InterestEventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestEventCategoryRepository extends JpaRepository<InterestEventCategory, String> {
    void deleteAllByInterestEvent(InterestEvent interestEvent);
}

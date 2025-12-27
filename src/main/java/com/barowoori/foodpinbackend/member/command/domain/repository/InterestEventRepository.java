package com.barowoori.foodpinbackend.member.command.domain.repository;

import com.barowoori.foodpinbackend.member.command.domain.model.InterestEvent;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestEventRepository extends JpaRepository<InterestEvent, String> {
    InterestEvent findByMember(Member member);
}

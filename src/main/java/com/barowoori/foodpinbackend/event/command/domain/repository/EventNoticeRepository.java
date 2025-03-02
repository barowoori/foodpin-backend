package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventNotice;
import com.barowoori.foodpinbackend.event.command.domain.repository.querydsl.EventNoticeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventNoticeRepository extends JpaRepository<EventNotice, String>, EventNoticeRepositoryCustom {
}

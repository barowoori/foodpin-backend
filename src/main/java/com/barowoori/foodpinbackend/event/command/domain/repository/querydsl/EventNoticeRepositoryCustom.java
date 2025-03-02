package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.event.command.domain.model.EventNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventNoticeRepositoryCustom {
    Page<EventNotice> findEventNoticeListByEventId(String eventId, Pageable pageable);
}

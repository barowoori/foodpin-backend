package com.barowoori.foodpinbackend.event.command.domain.service;

import com.barowoori.foodpinbackend.event.command.domain.model.EventContactAccessLog;
import com.barowoori.foodpinbackend.truck.command.domain.model.EventContactAccessLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventContactAccessLogService {
    private final EventContactAccessLogRepository eventContactAccessLogRepository;

    public EventContactAccessLogService(EventContactAccessLogRepository eventContactAccessLogRepository) {
        this.eventContactAccessLogRepository = eventContactAccessLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveEventContactAccessLog(EventContactAccessLog log) {
        eventContactAccessLogRepository.save(log);
    }
}

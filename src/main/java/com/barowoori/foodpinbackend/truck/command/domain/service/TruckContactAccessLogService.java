package com.barowoori.foodpinbackend.truck.command.domain.service;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckContactAccessLog;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckContactAccessLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TruckContactAccessLogService {
    private final TruckContactAccessLogRepository truckContactAccessLogRepository;

    public TruckContactAccessLogService(TruckContactAccessLogRepository truckContactAccessLogRepository) {
        this.truckContactAccessLogRepository = truckContactAccessLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveTruckContactAccessLog(TruckContactAccessLog log) {
        truckContactAccessLogRepository.save(log);
    }
}

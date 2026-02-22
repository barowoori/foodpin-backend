package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckContactAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckContactAccessLogRepository extends JpaRepository<TruckContactAccessLog, String> {
}

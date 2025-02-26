package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckManagerSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TruckManagerRepositoryCustom {
    Page<TruckManagerSummary> findTruckManagerPages(String truckId, String memberId, Pageable pageable);
}

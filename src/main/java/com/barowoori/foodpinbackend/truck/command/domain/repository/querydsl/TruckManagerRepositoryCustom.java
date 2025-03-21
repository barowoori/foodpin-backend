package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckManagerSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TruckManagerRepositoryCustom {
    Page<TruckManagerSummary> findTruckManagerPages(String truckId, String memberId, Pageable pageable);
    List<Truck> findOwnedTrucks(String memberId);
}

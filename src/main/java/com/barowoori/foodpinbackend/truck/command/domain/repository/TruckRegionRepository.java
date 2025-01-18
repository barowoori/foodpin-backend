package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckRegionRepository extends JpaRepository<TruckRegion, String> {
}

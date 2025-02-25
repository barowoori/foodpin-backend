package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckRegion;
import com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl.TruckRegionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckRegionRepository extends JpaRepository<TruckRegion, String>, TruckRegionRepositoryCustom {
}

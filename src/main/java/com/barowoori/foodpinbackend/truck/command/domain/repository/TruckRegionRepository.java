package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TruckRegionRepository extends JpaRepository<TruckRegion, String> {
    List<TruckRegion> findAllByTruck(Truck truck);
}

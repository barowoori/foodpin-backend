package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TruckCategoryRepository extends JpaRepository<TruckCategory, String>  {
    List<TruckCategory> findAllByTruck(Truck truck);
}

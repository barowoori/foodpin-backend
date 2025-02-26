package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckCategory;
import com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl.TruckCategoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TruckCategoryRepository extends JpaRepository<TruckCategory, String> , TruckCategoryRepositoryCustom {
    List<TruckCategory> findAllByTruck(Truck truck);
}

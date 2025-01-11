package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckCategoryRepository extends JpaRepository<TruckCategory, String>  {
}

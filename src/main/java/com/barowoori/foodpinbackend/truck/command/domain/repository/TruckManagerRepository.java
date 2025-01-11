package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManager;
import com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl.TruckManagerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckManagerRepository extends JpaRepository<TruckManager, String>, TruckManagerRepositoryCustom {
}

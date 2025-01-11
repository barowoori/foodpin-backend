package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckMenuRepository extends JpaRepository<TruckMenu, String> {
}

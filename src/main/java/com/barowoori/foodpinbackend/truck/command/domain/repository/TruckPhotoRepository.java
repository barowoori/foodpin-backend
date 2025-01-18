package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TruckPhotoRepository extends JpaRepository<TruckPhoto, String> {
    List<TruckPhoto> findByTruckOrderByCreateAt(Truck truck);
}

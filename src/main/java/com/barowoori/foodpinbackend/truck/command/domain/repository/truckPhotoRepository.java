package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface truckPhotoRepository extends JpaRepository<TruckPhoto, String> {
}

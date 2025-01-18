package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenuPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckMenuPhotoRepository extends JpaRepository<TruckMenuPhoto, String> {
}

package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenuPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TruckMenuPhotoRepository extends JpaRepository<TruckMenuPhoto, String> {
    List<TruckMenuPhoto> findAllByTruckMenu(TruckMenu truckMenu);
}

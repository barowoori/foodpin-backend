package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocumentPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TruckDocumentPhotoRepository extends JpaRepository<TruckDocumentPhoto, String> {
    List<TruckDocumentPhoto> findByTruckDocumentId(String truckDocumentId);
}

package com.barowoori.foodpinbackend.truck.command.domain.repository;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl.TruckDocumentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TruckDocumentRepository extends JpaRepository<TruckDocument, String>, TruckDocumentRepositoryCustom {
    TruckDocument findByTruckIdAndType(String truckId, DocumentType type);
}

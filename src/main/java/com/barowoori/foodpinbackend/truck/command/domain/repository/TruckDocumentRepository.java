package com.barowoori.foodpinbackend.truck.command.domain.repository;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocumentStatus;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.BackOfficeTruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl.TruckDocumentRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface TruckDocumentRepository extends JpaRepository<TruckDocument, String>, TruckDocumentRepositoryCustom {
    TruckDocument findByTruckIdAndType(String truckId, DocumentType type);
    Page<BackOfficeTruckDocument> getBackOfficeTruckDocuments(DocumentType type, String nickname, String phone, TruckDocumentStatus status,
                                                              LocalDate requestedStartAt, LocalDate requestedEndAt, LocalDate processedStartAt, LocalDate processedEndAt,
                                                              Pageable pageable);
    Map<String, List<String>> findPhotosByTruckDocumentIds(List<String> documentIds);
}

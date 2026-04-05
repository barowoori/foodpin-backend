package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocumentStatus;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.BackOfficeTruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentInfoDto;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TruckDocumentRepositoryCustom {
    TruckDocumentManager getDocumentManager(String truckId);
    BusinessRegistration getBusinessRegistrationDocumentByTruckId(String truckId);
    Map<String, List<TruckDocumentInfoDto>> getDocumentTypeByTruckIds(List<String> truckIds);
    List<TruckDocument> getTruckDocumentFiles(String truckId);
    Page<BackOfficeTruckDocument> getBackOfficeTruckDocuments(DocumentType type, String nickname, String phone, TruckDocumentStatus status,
                                                              LocalDate requestedStartAt, LocalDate requestedEndAt, LocalDate processedStartAt, LocalDate processedEndAt,
                                                              Pageable pageable);
    Map<String, List<String>> findPhotosByTruckDocumentIds(List<String> documentIds);
}

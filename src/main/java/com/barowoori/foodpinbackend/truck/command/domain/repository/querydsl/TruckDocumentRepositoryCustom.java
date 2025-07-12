package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentManager;

import java.util.List;
import java.util.Map;

public interface TruckDocumentRepositoryCustom {
    TruckDocumentManager getDocumentManager(String truckId);
    BusinessRegistration getBusinessRegistrationDocumentByTruckId(String truckId);
    Map<String, List<DocumentType>> getDocumentTypeByTruckIds(List<String> truckIds);
    List<TruckDocument> getTruckDocumentFiles(String truckId);
}

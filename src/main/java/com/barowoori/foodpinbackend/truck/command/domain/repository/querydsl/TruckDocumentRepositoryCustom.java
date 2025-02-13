package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentManager;

public interface TruckDocumentRepositoryCustom {
    TruckDocumentManager getDocumentManager(String truckId);
    BusinessRegistration getBusinessRegistrationDocumentByTruckId(String truckId);
}

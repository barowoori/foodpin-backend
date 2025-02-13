package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.document.command.domain.model.QBusinessRegistration;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentManager;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class TruckDocumentRepositoryCustomImpl implements TruckDocumentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public TruckDocumentRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public TruckDocumentManager getDocumentManager(String truckId) {
        QTruckDocument truckDocument = QTruckDocument.truckDocument;
        List<TruckDocument> documents = jpaQueryFactory.selectFrom(truckDocument)
                .where(truckDocument.truck.id.eq(truckId)).fetch();
        return new TruckDocumentManager(documents);
    }

    @Override
    public BusinessRegistration getBusinessRegistrationDocumentByTruckId(String truckId) {
        QTruckDocument truckDocument = QTruckDocument.truckDocument;
        QBusinessRegistration businessRegistration = QBusinessRegistration.businessRegistration;
        return jpaQueryFactory.select(businessRegistration)
                .from(truckDocument)
                .join(businessRegistration).on(businessRegistration.id.eq(truckDocument.documentId))
                .where(truckDocument.truck.id.eq(truckId)
                        .and(truckDocument.type.eq(DocumentType.BUSINESS_REGISTRATION)))
                .fetchOne();
    }
}

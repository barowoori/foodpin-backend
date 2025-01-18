package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

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
}

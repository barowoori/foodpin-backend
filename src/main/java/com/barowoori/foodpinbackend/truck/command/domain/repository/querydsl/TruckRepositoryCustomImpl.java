package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.truck.command.domain.model.QTruck;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckPhoto;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class TruckRepositoryCustomImpl implements TruckRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public TruckRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Truck getTruckWithPhotoById(String id) {
        QTruck truck = QTruck.truck;
        QTruckPhoto photo = QTruckPhoto.truckPhoto;
        return jpaQueryFactory.selectFrom(truck)
                .where(truck.id.eq(id))
                .join(truck.photos, photo).fetchJoin()
                .fetchOne();
    }

}

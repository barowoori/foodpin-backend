package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenuPhoto;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class TruckMenuRepositoryCustomImpl implements TruckMenuRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    public TruckMenuRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
    @Override
    public List<TruckMenu> getMenuListWithPhotoByTruckId(String truckId) {
        QTruckMenu menu = QTruckMenu.truckMenu;
        QTruckMenuPhoto menuPhoto = QTruckMenuPhoto.truckMenuPhoto;

        return jpaQueryFactory.selectFrom(menu)
                .leftJoin(menu.photos, menuPhoto).fetchJoin()
                .where(menu.truck.id.eq(truckId))
                .orderBy(menu.createAt.asc())
                .fetch();
    }
}

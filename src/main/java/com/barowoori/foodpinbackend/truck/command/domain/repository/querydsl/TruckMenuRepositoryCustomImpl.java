package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.file.command.domain.model.QFile;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruck;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenuPhoto;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TruckMenuRepositoryCustomImpl implements TruckMenuRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    public TruckMenuRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
    @Override
    public List<TruckMenu> getMenuListWithPhotoByTruckId(String truckId) {
        QTruckMenu menu = QTruckMenu.truckMenu;
        QTruckMenuPhoto menuPhoto = QTruckMenuPhoto.truckMenuPhoto;
        QFile file = QFile.file;

        return jpaQueryFactory.selectDistinct(menu)
                .from(menu)
                .leftJoin(menu.photos, menuPhoto).fetchJoin()
                .leftJoin(menuPhoto.file, file).fetchJoin()
                .where(menu.truck.id.eq(truckId))
                .orderBy(menu.createAt.desc())
                .fetch();
    }

    @Override
    public Map<String, List<String>> getMenuNamesByTruckIds(List<String> truckIds){
        QTruckMenu menu = QTruckMenu.truckMenu;
        QTruck truck = QTruck.truck;
        List<Tuple> results = jpaQueryFactory.select(truck.id,menu.name)
                .from(menu)
                .join(menu.truck, truck).on(menu.truck.id.in(truckIds))
                .fetch();

        return results.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(truck.id),
                        Collectors.mapping(tuple -> tuple.get(menu.name), Collectors.toList())
                ));
    }
}

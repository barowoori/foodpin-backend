package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.QCategory;
import com.barowoori.foodpinbackend.file.command.domain.model.QFile;
import com.barowoori.foodpinbackend.member.command.domain.model.QTruckLike;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruck.truck;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckDocument.truckDocument;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckManager.truckManager;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenu.truckMenu;

public class TruckRepositoryCustomImpl implements TruckRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public TruckRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Truck getTruckWithPhotoById(String id) {
        QTruck truck = QTruck.truck;
        QTruckPhoto photo = QTruckPhoto.truckPhoto;
        QFile file = QFile.file;
        return jpaQueryFactory.selectFrom(truck)
                .where(truck.id.eq(id))
                .join(truck.photos, photo).fetchJoin()
                .join(photo.file, file).fetchJoin()
                .fetchOne();
    }

    @Override
    public Page<Truck> findTruckListByFilter(String searchTerm, List<String> categoryCodes, Map<RegionType, List<String>> regionIds, Pageable pageable) {
        QTruck truck = QTruck.truck;
        QTruckRegion truckRegion = QTruckRegion.truckRegion;
        QTruckCategory truckCategory = QTruckCategory.truckCategory;
        QCategory category = QCategory.category;
        QTruckMenu truckMenu = QTruckMenu.truckMenu;
        QTruckDocument truckDocument = QTruckDocument.truckDocument;
        QTruckPhoto photo = QTruckPhoto.truckPhoto;
        QFile file = QFile.file;

        List<Truck> trucks = jpaQueryFactory.selectFrom(truck)
                .leftJoin(truckRegion).on(truckRegion.truck.eq(truck))
                .leftJoin(truckCategory).on(truckCategory.truck.eq(truck))
                .leftJoin(truckCategory.category, category)
                .leftJoin(truckMenu).on(truckMenu.truck.eq(truck))
                .leftJoin(truck.photos, photo).fetchJoin()
                .leftJoin
                        (photo.file, file)
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, regionIds, truck, truckMenu, category)
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_DO, regionIds.get(RegionType.REGION_DO)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_SI, regionIds.get(RegionType.REGION_SI)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_GU, regionIds.get(RegionType.REGION_GU)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_GUN, regionIds.get(RegionType.REGION_GUN)))
                                )
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long total = jpaQueryFactory.select(truck.count()).from(truck)
                .leftJoin(truckRegion).on(truckRegion.truck.eq(truck))
                .leftJoin(truckCategory).on(truckCategory.truck.eq(truck))
                .leftJoin(category).on(truckCategory.category.eq(category))
                .leftJoin(truckDocument).on(truckDocument.truck.eq(truck))
                .leftJoin(truckMenu).on(truckMenu.truck.eq(truck))
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, regionIds, truck, truckMenu, category)
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_DO, regionIds.get(RegionType.REGION_DO)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_SI, regionIds.get(RegionType.REGION_SI)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_GU, regionIds.get(RegionType.REGION_GU)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_GUN, regionIds.get(RegionType.REGION_GUN)))
                                )
                )
                .fetchOne();

        return new PageImpl<>(trucks, pageable, total);
    }

    @Override
    public Page<Truck> findLikeTruckListByFilter(String memberId, String searchTerm, List<String> categoryCodes, Map<RegionType, List<String>> regionIds, Pageable pageable) {
        QTruck truck = QTruck.truck;
        QTruckRegion truckRegion = QTruckRegion.truckRegion;
        QTruckCategory truckCategory = QTruckCategory.truckCategory;
        QCategory category = QCategory.category;
        QTruckMenu truckMenu = QTruckMenu.truckMenu;
        QTruckDocument truckDocument = QTruckDocument.truckDocument;
        QTruckPhoto photo = QTruckPhoto.truckPhoto;
        QFile file = QFile.file;
        QTruckLike truckLike = QTruckLike.truckLike;

        List<Truck> trucks = jpaQueryFactory.selectFrom(truck)
                .innerJoin(truckLike).on(truckLike.truck.eq(truck).and(truckLike.member.id.eq(memberId)))
                .leftJoin(truckRegion).on(truckRegion.truck.eq(truck))
                .leftJoin(truckCategory).on(truckCategory.truck.eq(truck))
                .leftJoin(truckCategory.category, category)
                .leftJoin(truckMenu).on(truckMenu.truck.eq(truck))
                .leftJoin(truck.photos, photo).fetchJoin()
                .leftJoin(photo.file, file)
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, regionIds, truck, truckMenu, category)
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_DO, regionIds.get(RegionType.REGION_DO)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_SI, regionIds.get(RegionType.REGION_SI)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_GU, regionIds.get(RegionType.REGION_GU)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_GUN, regionIds.get(RegionType.REGION_GUN)))
                                )
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long total = jpaQueryFactory.select(truck.count()).from(truck)
                .innerJoin(truckLike).on(truckLike.truck.eq(truck).and(truckLike.member.id.eq(memberId)))
                .leftJoin(truckRegion).on(truckRegion.truck.eq(truck))
                .leftJoin(truckCategory).on(truckCategory.truck.eq(truck))
                .leftJoin(category).on(truckCategory.category.eq(category))
                .leftJoin(truckDocument).on(truckDocument.truck.eq(truck))
                .leftJoin(truckMenu).on(truckMenu.truck.eq(truck))
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, regionIds, truck, truckMenu, category)
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_DO, regionIds.get(RegionType.REGION_DO)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_SI, regionIds.get(RegionType.REGION_SI)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_GU, regionIds.get(RegionType.REGION_GU)))
                                                .or(regionFilterCondition(truckRegion, RegionType.REGION_GUN, regionIds.get(RegionType.REGION_GUN)))
                                )
                )
                .fetchOne();

        return new PageImpl<>(trucks, pageable, total);
    }

    public BooleanBuilder createFilterBuilder(String searchTerm, List<String> categoryCodes, Map<RegionType, List<String>> regionIds,
                                              QTruck truck, QTruckMenu truckMenu, QCategory category) {
        BooleanBuilder filterBuilder = new BooleanBuilder();
        addSearchTermFilter(truck, truckMenu, searchTerm, filterBuilder);
        addCategoryFilter(category, categoryCodes, filterBuilder);

        return filterBuilder;
    }

    private void addSearchTermFilter(QTruck truck, QTruckMenu truckMenu, String searchTerm, BooleanBuilder builder) {
        if (searchTerm == null) {
            return;
        }
        builder.or(truck.name.contains(searchTerm));
        builder.or(truckMenu.name.contains(searchTerm));
    }

    private void addCategoryFilter(QCategory category, List<String> categoryCodes, BooleanBuilder builder) {
        if (categoryCodes == null || categoryCodes.isEmpty()) {
            return;
        }
        builder.or(category.code.in(categoryCodes));
    }

    private BooleanExpression regionFilterCondition(QTruckRegion truckRegion, RegionType regionType, List<String> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            return null;
        }
        return truckRegion.regionId.in(regionIds).and(truckRegion.regionType.eq(regionType));
    }

    private List<OrderSpecifier> getOrderSpecifier(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            PathBuilder orderByExpression = new PathBuilder(Truck.class, "truck");
            orders.add(new OrderSpecifier(direction, orderByExpression.get(order.getProperty())));
        });

        return orders;
    }
    @Override
    public Page<Truck> findApplicableTrucks(String memberId, Pageable pageable) {
        List<Truck> trucks = jpaQueryFactory.selectFrom(truck)
                .innerJoin(truckManager).on(truckManager.truck.eq(truck).and(truckManager.member.id.eq(memberId)))
                .leftJoin(truck.menus, truckMenu)
                .leftJoin(truck.documents, truckDocument)
                .where(truck.isDeleted.isFalse())
                .orderBy(truck.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total =  jpaQueryFactory.select(truck.count()).from(truck)
                .innerJoin(truckManager).on(truckManager.truck.eq(truck).and(truckManager.member.id.eq(memberId)))
                .leftJoin(truck.menus, truckMenu)
                .leftJoin(truck.documents, truckDocument)
                .where(truck.isDeleted.isFalse())
                .fetchOne();

        return new PageImpl<>(trucks, pageable, total);
    }
}

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

import static com.barowoori.foodpinbackend.category.command.domain.model.QCategory.category;
import static com.barowoori.foodpinbackend.file.command.domain.model.QFile.file;
import static com.barowoori.foodpinbackend.member.command.domain.model.QTruckLike.truckLike;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruck.truck;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckCategory.truckCategory;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckDocument.truckDocument;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckManager.truckManager;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenu.truckMenu;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckPhoto.truckPhoto;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckRegion.truckRegion;

public class TruckRepositoryCustomImpl implements TruckRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public TruckRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Truck getTruckWithPhotoById(String id) {
        QTruck truck = QTruck.truck;
        QTruckPhoto photo = truckPhoto;
        QFile file = QFile.file;
        return jpaQueryFactory.selectFrom(truck)
                .where(truck.id.eq(id))
                .join(truck.photos, photo).fetchJoin()
                .join(photo.file, file).fetchJoin()
                .fetchOne();
    }

    @Override
    public Page<Truck> findTruckListByFilter(String searchTerm, List<String> categoryCodes, Map<RegionType, List<String>> regionIds, Pageable pageable) {
        List<Truck> trucks = jpaQueryFactory.selectDistinct(truck)
                .from(truck)
                .leftJoin(truck.regions, truckRegion)
                .leftJoin(truck.categories, truckCategory)
                .leftJoin(truckCategory.category, category)
                .leftJoin(truck.menus, truckMenu)
                .leftJoin(truck.photos, truckPhoto)
                .leftJoin(truckPhoto.file, file)
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, regionIds, truck, truckMenu, category)
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(truck.countDistinct()).from(truck)
                .leftJoin(truck.regions, truckRegion)
                .leftJoin(truck.categories, truckCategory)
                .leftJoin(truckCategory.category, category)
                .leftJoin(truck.menus, truckMenu)
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, regionIds, truck, truckMenu, category)
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .fetchOne();

        return new PageImpl<>(trucks, pageable, total);
    }

    @Override
    public Page<Truck> findLikeTruckListByFilter(String memberId, String searchTerm, List<String> categoryCodes, Map<RegionType, List<String>> regionIds, Pageable pageable) {
        List<Truck> trucks = jpaQueryFactory.selectDistinct(truck)
                .from(truck)
                .innerJoin(truckLike).on(truckLike.truck.eq(truck).and(truckLike.member.id.eq(memberId)))
                .leftJoin(truck.regions, truckRegion)
                .leftJoin(truck.categories, truckCategory)
                .leftJoin(truckCategory.category, category)
                .leftJoin(truckMenu).on(truckMenu.truck.eq(truck))
                .leftJoin(truck.photos, truckPhoto).fetchJoin()
                .leftJoin(truckPhoto.file, file).fetchJoin()
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, regionIds, truck, truckMenu, category)
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long total = jpaQueryFactory.select(truck.countDistinct()).from(truck)
                .innerJoin(truckLike).on(truckLike.truck.eq(truck).and(truckLike.member.id.eq(memberId)))
                .leftJoin(truck.regions, truckRegion)
                .leftJoin(truck.categories, truckCategory)
                .leftJoin(category).on(truckCategory.category.eq(category))
                .leftJoin(truckDocument).on(truckDocument.truck.eq(truck))
                .leftJoin(truckMenu).on(truckMenu.truck.eq(truck))
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, regionIds, truck, truckMenu, category)
                                                .and(regionFilterCondition(regionIds))
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
        builder.and(truck.name.contains(searchTerm)).or(truckMenu.name.contains(searchTerm));
    }

    private void addCategoryFilter(QCategory category, List<String> categoryCodes, BooleanBuilder builder) {
        if (categoryCodes == null || categoryCodes.isEmpty()) {
            return;
        }
        builder.and(category.code.in(categoryCodes));
    }
    private BooleanExpression regionFilterCondition(Map<RegionType, List<String>> regionIds){
        if (regionIds == null || regionIds.isEmpty()) {
            return null;
        }
        return truckRegion.regionId.in(regionIds.get(RegionType.REGION_DO)).and(truckRegion.regionType.eq(RegionType.REGION_DO))
                .or(truckRegion.regionId.in(regionIds.get(RegionType.REGION_SI)).and(truckRegion.regionType.eq(RegionType.REGION_SI)))
                .or(truckRegion.regionId.in(regionIds.get(RegionType.REGION_GU)).and(truckRegion.regionType.eq(RegionType.REGION_GU)))
                .or(truckRegion.regionId.in(regionIds.get(RegionType.REGION_GUN)).and(truckRegion.regionType.eq(RegionType.REGION_GUN)));
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

        Long total =  jpaQueryFactory.select(truck.countDistinct()).from(truck)
                .innerJoin(truckManager).on(truckManager.truck.eq(truck).and(truckManager.member.id.eq(memberId)))
                .leftJoin(truck.menus, truckMenu)
                .leftJoin(truck.documents, truckDocument)
                .where(truck.isDeleted.isFalse())
                .fetchOne();

        return new PageImpl<>(trucks, pageable, total);
    }
}

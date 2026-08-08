package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.QCategory;
import com.barowoori.foodpinbackend.file.command.domain.model.QFile;
import com.barowoori.foodpinbackend.member.command.domain.model.QTruckLike;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckProjection;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

import static com.barowoori.foodpinbackend.category.command.domain.model.QCategory.category;
import static com.barowoori.foodpinbackend.file.command.domain.model.QFile.file;
import static com.barowoori.foodpinbackend.member.command.domain.model.QTruckLike.truckLike;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruck.truck;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckCategory.truckCategory;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckDocument.truckDocument;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckManager.truckManager;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenu.truckMenu;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenuPhoto.truckMenuPhoto;
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
    public Integer findMaxAvgMenuPrice() {
        return jpaQueryFactory.select(truck.avgMenuPrice.max())
                .from(truck)
                .where(truck.isDeleted.isFalse())
                .fetchOne();
    }

    @Override
    public Page<TruckProjection> findTruckListByFilter(String searchTerm, List<String> categoryCodes, Map<RegionType, List<String>> regionIds,
                                                       Set<TruckType> types, Integer minAvgMenuPrice, Integer maxAvgMenuPrice, Set<TruckColor> colors, Set<TruckBodyType> bodyTypes,
                                                       Set<PaymentMethod> paymentMethods, Set<ProofIssuanceType> proofIssuanceTypes, Boolean isCatering,
                                                       Pageable pageable) {
        List<Tuple> result = jpaQueryFactory.selectDistinct(truck.id, truck.createdAt, truck.views)
                .from(truck)
                .leftJoin(truck.regions, truckRegion)
                .leftJoin(truck.categories, truckCategory)
                .leftJoin(truckCategory.category, category)
                .leftJoin(truck.menus, truckMenu)
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes,
                                                types, minAvgMenuPrice, maxAvgMenuPrice, colors, bodyTypes, paymentMethods, proofIssuanceTypes, isCatering,
                                                truck, category, truckMenu
                                        )
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<String> truckIds = result.stream()
                .map(tuple -> tuple.get(truck.id))
                .collect(Collectors.toList());

        List<TruckProjection> projections = loadTruckProjections(truckIds);

        Long total = jpaQueryFactory.select(truck.countDistinct()).from(truck)
                .leftJoin(truck.regions, truckRegion)
                .leftJoin(truck.categories, truckCategory)
                .leftJoin(truckCategory.category, category)
                .leftJoin(truck.menus, truckMenu)
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes,
                                                types, minAvgMenuPrice, maxAvgMenuPrice, colors, bodyTypes, paymentMethods, proofIssuanceTypes, isCatering,
                                                truck, category, truckMenu
                                        )
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .fetchOne();

        return new PageImpl<>(projections, pageable, total);
    }

    @Override
    public Page<TruckProjection> findLikeTruckListByFilter(String memberId, String searchTerm, List<String> categoryCodes, Map<RegionType, List<String>> regionIds,
                                                           Set<TruckType> types, Integer minAvgMenuPrice, Integer maxAvgMenuPrice, Set<TruckColor> colors, Set<TruckBodyType> bodyTypes,
                                                           Set<PaymentMethod> paymentMethods, Set<ProofIssuanceType> proofIssuanceTypes, Boolean isCatering,
                                                           Pageable pageable) {
        List<Tuple> result = jpaQueryFactory.selectDistinct(truck.id, truck.createdAt, truck.views)
                .from(truck)
                .innerJoin(truckLike).on(truckLike.truck.eq(truck).and(truckLike.member.id.eq(memberId)))
                .leftJoin(truck.regions, truckRegion)
                .leftJoin(truck.categories, truckCategory)
                .leftJoin(truckCategory.category, category)
                .leftJoin(truck.menus, truckMenu)
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes,
                                                types, minAvgMenuPrice, maxAvgMenuPrice, colors, bodyTypes, paymentMethods, proofIssuanceTypes, isCatering,
                                                truck, category, truckMenu
                                        )
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<String> truckIds = result.stream()
                .map(tuple -> tuple.get(truck.id))
                .collect(Collectors.toList());

        List<TruckProjection> projections = loadTruckProjections(truckIds);

        Long total = jpaQueryFactory.select(truck.countDistinct()).from(truck)
                .innerJoin(truckLike).on(truckLike.truck.eq(truck).and(truckLike.member.id.eq(memberId)))
                .leftJoin(truck.regions, truckRegion)
                .leftJoin(truck.categories, truckCategory)
                .leftJoin(truckCategory.category, category)
                .leftJoin(truck.menus, truckMenu)
                .where(
                        truck.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes,
                                                types, minAvgMenuPrice, maxAvgMenuPrice, colors, bodyTypes, paymentMethods, proofIssuanceTypes, isCatering,
                                                truck, category, truckMenu
                                        )
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .fetchOne();

        return new PageImpl<>(projections, pageable, total);
    }

    @Override
    public Page<TruckProjection> findBackOfficeTruckListByFilter(String searchTerm, List<String> categoryCodes, Map<RegionType, List<String>> regionIds,
                                                                 Set<TruckType> types, Integer minAvgMenuPrice, Integer maxAvgMenuPrice, Set<TruckColor> colors, Set<TruckBodyType> bodyTypes,
                                                                 Set<PaymentMethod> paymentMethods, Set<ProofIssuanceType> proofIssuanceTypes, Boolean isCatering, Boolean isDeleted,
                                                                 Pageable pageable) {
        BooleanBuilder filterBuilder = createFilterBuilder(searchTerm, categoryCodes,
                types, minAvgMenuPrice, maxAvgMenuPrice, colors, bodyTypes, paymentMethods, proofIssuanceTypes, isCatering,
                truck, category, truckMenu);
        BooleanExpression deletedFilter = isDeletedEq(isDeleted);
        if (deletedFilter != null) {
            filterBuilder.and(deletedFilter);
        }
        BooleanExpression regionFilter = regionFilterCondition(regionIds);
        if (regionFilter != null) {
            filterBuilder.and(regionFilter);
        }

        List<Tuple> result = jpaQueryFactory.selectDistinct(truck.id, truck.createdAt, truck.views)
                .from(truck)
                .leftJoin(truck.regions, truckRegion)
                .leftJoin(truck.categories, truckCategory)
                .leftJoin(truckCategory.category, category)
                .leftJoin(truck.menus, truckMenu)
                .where(filterBuilder)
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<String> truckIds = result.stream()
                .map(tuple -> tuple.get(truck.id))
                .collect(Collectors.toList());

        List<TruckProjection> projections = loadTruckProjections(truckIds);

        Long total = jpaQueryFactory.select(truck.countDistinct()).from(truck)
                .leftJoin(truck.regions, truckRegion)
                .leftJoin(truck.categories, truckCategory)
                .leftJoin(truckCategory.category, category)
                .leftJoin(truck.menus, truckMenu)
                .where(filterBuilder)
                .fetchOne();

        return new PageImpl<>(projections, pageable, total == null ? 0 : total);
    }

    private List<TruckProjection> loadTruckProjections(List<String> truckIds) {
        if (truckIds.isEmpty()) return Collections.emptyList();

        // Query 2a: basic truck info (id, name, avgMenuPrice)
        Map<String, Tuple> basicByTruck = jpaQueryFactory
                .select(truck.id, truck.name, truck.avgMenuPrice)
                .from(truck)
                .where(truck.id.in(truckIds))
                .fetch()
                .stream()
                .collect(Collectors.toMap(t -> t.get(truck.id), t -> t));

        // Query 2b: first truck photo path per truck (ordered by created_at)
        Map<String, String> mainPhotoByTruck = new LinkedHashMap<>();
        jpaQueryFactory
                .select(truckPhoto.truck.id, file.path)
                .from(truckPhoto)
                .join(truckPhoto.file, file)
                .where(truckPhoto.truck.id.in(truckIds))
                .orderBy(truckPhoto.createAt.asc())
                .fetch()
                .forEach(t -> mainPhotoByTruck.putIfAbsent(t.get(truckPhoto.truck.id), t.get(file.path)));

        // Query 2c: menu names per truck (ordered by created_at)
        Map<String, List<String>> menuNamesByTruck = new LinkedHashMap<>();
        jpaQueryFactory
                .select(truckMenu.truck.id, truckMenu.name)
                .from(truckMenu)
                .where(truckMenu.truck.id.in(truckIds))
                .orderBy(truckMenu.createAt.asc())
                .fetch()
                .forEach(t -> menuNamesByTruck
                        .computeIfAbsent(t.get(truckMenu.truck.id), k -> new ArrayList<>())
                        .add(t.get(truckMenu.name)));

        // Query 2d: menu photo paths per truck (first 2 per truck, ordered by menu.created_at, photo.created_at)
        Map<String, List<String>> menuPhotosByTruck = new LinkedHashMap<>();
        jpaQueryFactory
                .select(truckMenu.truck.id, file.path)
                .from(truckMenuPhoto)
                .join(truckMenuPhoto.truckMenu, truckMenu)
                .join(truckMenuPhoto.file, file)
                .where(truckMenu.truck.id.in(truckIds))
                .orderBy(truckMenu.createAt.asc(), truckMenuPhoto.createAt.asc())
                .fetch()
                .forEach(t -> {
                    String truckId = t.get(truckMenu.truck.id);
                    List<String> paths = menuPhotosByTruck.computeIfAbsent(truckId, k -> new ArrayList<>());
                    if (paths.size() < 2) {
                        paths.add(t.get(file.path));
                    }
                });

        // Query 2e: regions per truck (ordered by created_at)
        Map<String, List<TruckProjection.RegionEntry>> regionsByTruck = new LinkedHashMap<>();
        jpaQueryFactory
                .select(truckRegion.truck.id, truckRegion.regionType, truckRegion.regionId)
                .from(truckRegion)
                .where(truckRegion.truck.id.in(truckIds))
                .orderBy(truckRegion.createAt.asc())
                .fetch()
                .forEach(t -> regionsByTruck
                        .computeIfAbsent(t.get(truckRegion.truck.id), k -> new ArrayList<>())
                        .add(new TruckProjection.RegionEntry(t.get(truckRegion.regionType), t.get(truckRegion.regionId))));

        return truckIds.stream().map(id -> {
            Tuple basic = basicByTruck.get(id);
            return new TruckProjection(
                    id,
                    basic != null ? basic.get(truck.name) : null,
                    basic != null ? basic.get(truck.avgMenuPrice) : null,
                    mainPhotoByTruck.get(id),
                    menuNamesByTruck.getOrDefault(id, Collections.emptyList()),
                    menuPhotosByTruck.getOrDefault(id, Collections.emptyList()),
                    regionsByTruck.getOrDefault(id, Collections.emptyList())
            );
        }).toList();
    }

    public BooleanBuilder createFilterBuilder(String searchTerm,
                                              List<String> categoryCodes,
                                              Set<TruckType> types,
                                              Integer minAvgMenuPrice,
                                              Integer maxAvgMenuPrice,
                                              Set<TruckColor> colors,
                                              Set<TruckBodyType> bodyTypes,
                                              Set<PaymentMethod> paymentMethods,
                                              Set<ProofIssuanceType> proofIssuanceTypes,
                                              Boolean isCatering,
                                              QTruck truck,
                                              QCategory category,
                                              QTruckMenu truckMenu) {

        BooleanBuilder builder = new BooleanBuilder();
        addSearchTermFilter(truck, truckMenu, searchTerm, builder);
        addCategoryFilter(category, categoryCodes, builder);

        addTypeFilter(types, builder);
        addBodyTypeFilter(truck, bodyTypes, builder);
        addAvgMenuPriceFilter(truck, minAvgMenuPrice, maxAvgMenuPrice, builder);
        addColorFilter(colors, builder);
        addPaymentMethodFilter(paymentMethods, builder);
        addProofIssuanceTypeFilter(proofIssuanceTypes, builder);
        addCateringFilter(isCatering, builder);
        return builder;
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
    private void addTypeFilter(Set<TruckType> types, BooleanBuilder builder) {
        if (types == null || types.isEmpty()) {
            return;
        }

        BooleanBuilder typeBuilder = new BooleanBuilder();

        StringPath typesPath = Expressions.stringPath("types");

        for (TruckType type : types) {
            typeBuilder.or(
                    Expressions.stringTemplate(
                            "CONCAT(',', {0}, ',')",
                            typesPath
                    ).contains("," + type.name() + ",")
            );
        }

        builder.and(typeBuilder);
    }

    private void addBodyTypeFilter(QTruck truck, Set<TruckBodyType> types, BooleanBuilder builder) {
        if (types == null || types.isEmpty()) {
            return;
        }
        builder.and(truck.bodyType.in(types));
    }

    private void addAvgMenuPriceFilter(
            QTruck truck,
            Integer min,
            Integer max,
            BooleanBuilder builder
    ) {
        if (min != null) {
            builder.and(truck.avgMenuPrice.goe(min));
        }
        if (max != null) {
            builder.and(truck.avgMenuPrice.loe(max));
        }
    }

    private void addColorFilter(Set<TruckColor> colors, BooleanBuilder builder) {
        if (colors == null || colors.isEmpty()) {
            return;
        }

        BooleanBuilder colorBuilder = new BooleanBuilder();

        for (TruckColor color : colors) {
            colorBuilder.or(
                    Expressions.stringTemplate(
                            "CONCAT(',', {0}, ',')",
                            QTruck.truck.colors
                    ).contains("," + color.name() + ",")
            );
        }

        builder.and(colorBuilder);
    }

    private void addPaymentMethodFilter(Set<PaymentMethod> paymentMethods, BooleanBuilder builder) {
        if (paymentMethods == null || paymentMethods.isEmpty()) {
            return;
        }

        BooleanBuilder paymentBuilder = new BooleanBuilder();
        for (PaymentMethod method : paymentMethods) {
            paymentBuilder.or(
                    Expressions.stringTemplate(
                            "CONCAT(',', {0}, ',')",
                            QTruck.truck.paymentMethods
                    ).contains("," + method.name() + ",")
            );
        }

        builder.and(paymentBuilder);
    }

    private void addProofIssuanceTypeFilter(Set<ProofIssuanceType> proofIssuanceTypes, BooleanBuilder builder) {
        if (proofIssuanceTypes == null || proofIssuanceTypes.isEmpty()) {
            return;
        }

        BooleanBuilder proofBuilder = new BooleanBuilder();
        for (ProofIssuanceType type : proofIssuanceTypes) {
            proofBuilder.or(
                    Expressions.stringTemplate(
                            "CONCAT(',', {0}, ',')",
                            QTruck.truck.proofIssuanceTypes
                    ).contains("," + type.name() + ",")
            );
        }

        builder.and(proofBuilder);
    }

    private void addCateringFilter(Boolean isCatering, BooleanBuilder builder){
        if (isCatering == null) {
            return;
        }
        builder.and(truck.isCatering.eq(isCatering));
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

    private BooleanExpression isDeletedEq(Boolean isDeleted) {
        if (isDeleted == null) {
            return null;
        }
        return truck.isDeleted.eq(isDeleted);
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
        List<Truck> trucks = jpaQueryFactory.selectDistinct(truck)
                .from(truck)
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

    @Override
    public List<Truck> findAllApplicableTrucks(String memberId) {
        return jpaQueryFactory.selectDistinct(truck)
                .from(truck)
                .innerJoin(truckManager).on(truckManager.truck.eq(truck).and(truckManager.member.id.eq(memberId)))
                .leftJoin(truck.documents, truckDocument).fetchJoin()
                .where(truck.isDeleted.isFalse())
                .orderBy(truck.name.asc())
                .fetch();
    }
}

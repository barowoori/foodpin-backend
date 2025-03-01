package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.QCategory;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.file.command.domain.model.QFile;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruck;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckRegion;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.barowoori.foodpinbackend.category.command.domain.model.QCategory.category;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEvent.event;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventCategory.eventCategory;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventDate.eventDate;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventDocument.eventDocument;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventPhoto.eventPhoto;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventRecruitDetail.eventRecruitDetail;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventRegion.eventRegion;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventView.eventView;
import static com.barowoori.foodpinbackend.file.command.domain.model.QFile.file;

public class EventRepositoryCustomImpl implements EventRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public EventRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Event findEventDetail(String eventId){
        return jpaQueryFactory.selectFrom(event)
                .leftJoin(event.regions, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .leftJoin(event.recruitDetail, eventRecruitDetail)
                .leftJoin(event.view, eventView)
                .leftJoin(event.photos, eventPhoto)
                .leftJoin(eventPhoto.file, file)
                .leftJoin(event.documents, eventDocument)
                .where(event.id.eq(eventId).and(event.isDeleted.isFalse()))
                .fetchOne();
    }

    public Page<Event> findEventListByFilter(String searchTerm, Map<RegionType, List<String>> regionIds,
                                             LocalDate startDate, LocalDate endDate,
                                             List<String> categoryCodes, Pageable pageable) {
        List<Event> events = jpaQueryFactory.selectFrom(event)
                .leftJoin(event.regions, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .leftJoin(event.recruitDetail, eventRecruitDetail)
                .leftJoin(event.view, eventView)
                .leftJoin(event.photos, eventPhoto)
                .leftJoin(eventPhoto.file, file)
                .where(
                        event.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, event, eventDate, category)
                                                .or(regionFilterCondition(eventRegion, RegionType.REGION_DO, regionIds.get(RegionType.REGION_DO)))
                                                .or(regionFilterCondition(eventRegion, RegionType.REGION_SI, regionIds.get(RegionType.REGION_SI)))
                                                .or(regionFilterCondition(eventRegion, RegionType.REGION_GU, regionIds.get(RegionType.REGION_GU)))
                                                .or(regionFilterCondition(eventRegion, RegionType.REGION_GUN, regionIds.get(RegionType.REGION_GUN)))
                                )
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(event.count()).from(event)
                .leftJoin(event.regions, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .leftJoin(event.recruitDetail, eventRecruitDetail)
                .where(
                        event.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, event, eventDate, category)
                                                .or(regionFilterCondition(eventRegion, RegionType.REGION_DO, regionIds.get(RegionType.REGION_DO)))
                                                .or(regionFilterCondition(eventRegion, RegionType.REGION_SI, regionIds.get(RegionType.REGION_SI)))
                                                .or(regionFilterCondition(eventRegion, RegionType.REGION_GU, regionIds.get(RegionType.REGION_GU)))
                                                .or(regionFilterCondition(eventRegion, RegionType.REGION_GUN, regionIds.get(RegionType.REGION_GUN)))
                                )
                )
                .fetchOne();

        return new PageImpl<>(events, pageable, total);
    }

    public BooleanBuilder createFilterBuilder(String searchTerm, List<String> categoryCodes, LocalDate startDate, LocalDate endDate,
                                              QEvent event, QEventDate eventDate, QCategory category) {
        BooleanBuilder filterBuilder = new BooleanBuilder();
        addSearchTermFilter(event, searchTerm, filterBuilder);
        addCategoryFilter(category, categoryCodes, filterBuilder);
        addPeriodFilter(eventDate, startDate, endDate, filterBuilder);

        return filterBuilder;
    }

    private void addSearchTermFilter(QEvent event, String searchTerm, BooleanBuilder builder) {
        if (searchTerm == null) {
            return;
        }
        builder.or(event.name.contains(searchTerm));
    }

    private void addCategoryFilter(QCategory category, List<String> categoryCodes, BooleanBuilder builder) {
        if (categoryCodes == null || categoryCodes.isEmpty()) {
            return;
        }
        builder.or(category.code.in(categoryCodes));
    }

    private void addPeriodFilter(QEventDate eventDate, LocalDate startDate, LocalDate endDate, BooleanBuilder builder) {
        if (startDate == null || endDate == null) {
            return;
        }
        builder.or(eventDate.date.between(startDate, endDate));
    }

    private BooleanExpression regionFilterCondition(QEventRegion eventRegion, RegionType regionType, List<String> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            return null;
        }
        return eventRegion.regionId.in(regionIds).and(eventRegion.regionType.eq(regionType));
    }

    private List<OrderSpecifier> getOrderSpecifier(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        PathBuilder<Event> eventPathBuilder = new PathBuilder<>(Event.class, "event");
        PathBuilder<EventView> eventViewPathBuilder = new PathBuilder<>(EventView.class, "eventView");
        PathBuilder<EventRecruitDetail> eventRecruitDetailPathBuilder = new PathBuilder<>(EventRecruitDetail.class, "eventRecruitDetail");

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            if (order.getProperty().equals("createdAt")) {
                orders.add(new OrderSpecifier(direction, eventPathBuilder.get(order.getProperty())));
            } else if (order.getProperty().equals("views")) {
                orders.add(new OrderSpecifier(direction, eventViewPathBuilder.get(order.getProperty())));
            } else if (order.getProperty().equals("applicant")) {
                orders.add(new OrderSpecifier(direction, eventRecruitDetailPathBuilder.get("applicantCount")));
            } else if (order.getProperty().equals("close")) {
                orders.add(new OrderSpecifier(direction, eventRecruitDetailPathBuilder.get("recruitEndDate")));
            }
        });

        return orders;
    }
}

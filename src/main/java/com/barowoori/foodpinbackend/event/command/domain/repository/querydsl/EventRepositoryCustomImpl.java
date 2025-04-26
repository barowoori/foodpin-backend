package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.QCategory;
import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventManageList;
import com.barowoori.foodpinbackend.file.command.domain.model.QFile;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruck;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckRegion;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
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
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventTruck.eventTruck;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventView.eventView;
import static com.barowoori.foodpinbackend.file.command.domain.model.QFile.file;
import static com.barowoori.foodpinbackend.member.command.domain.model.QEventLike.eventLike;
import static com.barowoori.foodpinbackend.member.command.domain.model.QMember.member;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckRegion.truckRegion;

public class EventRepositoryCustomImpl implements EventRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public EventRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Event findEventDetail(String eventId) {
        return jpaQueryFactory.selectFrom(event)
                .leftJoin(event.eventRegion, eventRegion)
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

    @Override
    public Page<Event> findEventListByFilter(String searchTerm, Map<RegionType, List<String>> regionIds,
                                             LocalDate startDate, LocalDate endDate,
                                             List<String> categoryCodes, Pageable pageable) {
        List<Event> events = jpaQueryFactory.selectFrom(event)
                .leftJoin(event.eventRegion, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .leftJoin(event.view, eventView)
                .leftJoin(event.photos, eventPhoto)
                .leftJoin(eventPhoto.file, file)
                .where(
                        event.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, event, eventDate, category)
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(event.countDistinct()).from(event)
                .leftJoin(event.eventRegion, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .where(
                        event.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, event, eventDate, category)
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .fetchOne();

        return new PageImpl<>(events, pageable, total);
    }

    @Override
    public Page<Event> findLikeEventListByFilter(String memberId, String searchTerm, Map<RegionType, List<String>> regionIds,
                                                 LocalDate startDate, LocalDate endDate,
                                                 List<String> categoryCodes, Pageable pageable) {
        List<Event> events = jpaQueryFactory.selectFrom(event)
                .innerJoin(eventLike).on(eventLike.event.eq(event).and(eventLike.member.id.eq(memberId)))
                .leftJoin(event.eventRegion, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .leftJoin(event.view, eventView)
                .leftJoin(event.photos, eventPhoto)
                .leftJoin(eventPhoto.file, file)
                .where(
                        event.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, event, eventDate, category)
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(event.countDistinct()).from(event)
                .innerJoin(eventLike).on(eventLike.event.eq(event).and(eventLike.member.id.eq(memberId)))
                .leftJoin(event.eventRegion, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .where(
                        event.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, event, eventDate, category)
                                                .and(regionFilterCondition(regionIds))
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
        builder.and(event.name.contains(searchTerm));
    }

    private void addCategoryFilter(QCategory category, List<String> categoryCodes, BooleanBuilder builder) {
        if (categoryCodes == null || categoryCodes.isEmpty()) {
            return;
        }
        builder.and(category.code.in(categoryCodes));
    }

    private void addPeriodFilter(QEventDate eventDate, LocalDate startDate, LocalDate endDate, BooleanBuilder builder) {
        if (startDate == null || endDate == null) {
            return;
        }
        builder.and(eventDate.date.between(startDate, endDate));
    }

    private BooleanExpression regionFilterCondition(Map<RegionType, List<String>> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            return null;
        }
        return eventRegion.regionId.in(regionIds.get(RegionType.REGION_DO)).and(eventRegion.regionType.eq(RegionType.REGION_DO))
                .or(eventRegion.regionId.in(regionIds.get(RegionType.REGION_SI)).and(eventRegion.regionType.eq(RegionType.REGION_SI)))
                .or(eventRegion.regionId.in(regionIds.get(RegionType.REGION_GU)).and(eventRegion.regionType.eq(RegionType.REGION_GU)))
                .or(eventRegion.regionId.in(regionIds.get(RegionType.REGION_GUN)).and(eventRegion.regionType.eq(RegionType.REGION_GUN)));
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

    @Override
    public Page<Event> findProgressEventManageList(String memberId, String status, Pageable pageable) {
        List<Event> events = jpaQueryFactory.selectFrom(event)
                .leftJoin(event.eventRegion, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .leftJoin(event.view, eventView)
                .leftJoin(event.photos, eventPhoto)
                .leftJoin(eventPhoto.file, file)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.createdBy.eq(memberId))
                                .and(createProgressFilterBuilder(status))
                )
                .orderBy(event.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(event.countDistinct()).from(event)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.createdBy.eq(memberId))
                                .and(createProgressFilterBuilder(status))
                )
                .fetchOne();

        return new PageImpl<>(events, pageable, total);
    }

    @Override
    public Page<Event> findCompletedEventManageList(String memberId, String status, Pageable pageable) {
        List<Event> events = jpaQueryFactory.selectFrom(event)
                .leftJoin(event.eventRegion, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .leftJoin(event.view, eventView)
                .leftJoin(event.photos, eventPhoto)
                .leftJoin(eventPhoto.file, file)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.createdBy.eq(memberId))
                                .and(createCompletedFilterBuilder(status))
                )
                .orderBy(event.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(event.countDistinct()).from(event)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.createdBy.eq(memberId))
                                .and(createCompletedFilterBuilder(status))
                )
                .fetchOne();

        return new PageImpl<>(events, pageable, total);
    }

    public BooleanBuilder createProgressFilterBuilder(String status) {
        BooleanBuilder filterBuilder = new BooleanBuilder();
        if (status.equals("ALL")) {
            filterBuilder
                    .and(event.status.in(EventStatus.RECRUITING, EventStatus.SELECTING, EventStatus.IN_PROGRESS));
        } else {
            filterBuilder.and(event.status.eq(EventStatus.valueOf(status)));
        }
        return filterBuilder;
    }

    public BooleanBuilder createCompletedFilterBuilder(String status) {
        BooleanBuilder filterBuilder = new BooleanBuilder();
        if (status.equals("ALL")) {
            filterBuilder.and(event.status.in(EventStatus.COMPLETED, EventStatus.RECRUITMENT_CANCELLED));
        } else {
            filterBuilder.and(event.status.eq(EventStatus.valueOf(status)));
        }
        return filterBuilder;
    }

    @Override
    public MemberFcmInfoDto findEventCreatorFcmInfo(String eventId) {
        return jpaQueryFactory
                .select(Projections.constructor(MemberFcmInfoDto.class, member.id, member.fcmToken))
                .from(event)
                .innerJoin(member).on(event.createdBy.eq(member.id))
                .where(event.id.eq(eventId))
                .fetchOne();
    }
}

package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.QCategory;
import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventDashboardCount;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.MemberForEventFcmInfoDto;
import com.barowoori.foodpinbackend.member.command.domain.model.EventCreatorType;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import static com.barowoori.foodpinbackend.member.command.domain.model.QEventLike.eventLike;
import static com.barowoori.foodpinbackend.member.command.domain.model.QMember.member;

public class EventRepositoryCustomImpl implements EventRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public EventRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Event findEventDetail(String eventId) {
        return jpaQueryFactory.selectDistinct(event)
                .from(event)
                .leftJoin(event.recruitDetail, eventRecruitDetail).fetchJoin()
                .leftJoin(event.view, eventView).fetchJoin()
                .leftJoin(event.photos, eventPhoto).fetchJoin()
                .leftJoin(eventPhoto.file, file).fetchJoin()
                .where(event.id.eq(eventId).and(event.isDeleted.isFalse()))
                .fetchOne();
    }

    @Override
    public Page<Event> findEventListByFilter(String searchTerm, Map<RegionType, List<String>> regionIds,
                                             LocalDate startDate, LocalDate endDate,
                                             List<String> categoryCodes,
                                             EventType type, Set<TruckType> truckTypes, Boolean isCatering, List<EventRecruitingStatus> recruitingStatuses,
                                             Pageable pageable) {
        List<String> eventIds = jpaQueryFactory.select(event.id).distinct()
                .from(event)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.isHidden.isFalse())
                                .and(eventRecruitDetail.recruitingStatus.in(recruitingStatuses))
                                .and(createListFilterBuilder(searchTerm, regionIds, startDate, endDate, categoryCodes, type, truckTypes, isCatering, null, null))
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(event.id.countDistinct()).from(event)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.isHidden.isFalse())
                                .and(eventRecruitDetail.recruitingStatus.in(recruitingStatuses))
                                .and(createListFilterBuilder(searchTerm, regionIds, startDate, endDate, categoryCodes, type, truckTypes, isCatering, null, null))
                )
                .fetchOne();

        return new PageImpl<>(findEventsByIds(eventIds), pageable, total);
    }

    @Override
    public Page<Event> findBackOfficeEventListByFilter(String searchTerm, Map<RegionType, List<String>> regionIds,
                                                       LocalDate startDate, LocalDate endDate,
                                                       List<String> categoryCodes,
                                                       EventType type, Set<TruckType> truckTypes, Boolean isCatering,
                                                       LocalDate recruitEndDateFrom, LocalDate recruitEndDateTo,
                                                       Pageable pageable) {
        List<String> eventIds = jpaQueryFactory.select(event.id).distinct()
                .from(event)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.creatorType.eq(EventCreatorType.ADMIN))
                                .and(createListFilterBuilder(searchTerm, regionIds, startDate, endDate, categoryCodes, type, truckTypes, isCatering, recruitEndDateFrom, recruitEndDateTo))
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(event.id.countDistinct()).from(event)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.creatorType.eq(EventCreatorType.ADMIN))
                                .and(createListFilterBuilder(searchTerm, regionIds, startDate, endDate, categoryCodes, type, truckTypes, isCatering, recruitEndDateFrom, recruitEndDateTo))
                )
                .fetchOne();

        return new PageImpl<>(findEventsByIds(eventIds), pageable, total);
    }

    @Override
    public Page<Event> findLikeEventListByFilter(String memberId, String searchTerm, Map<RegionType, List<String>> regionIds,
                                                 LocalDate startDate, LocalDate endDate,
                                                 List<String> categoryCodes,
                                                 EventType type, Set<TruckType> truckTypes, Boolean isCatering,
                                                 Pageable pageable) {
        List<String> eventIds = jpaQueryFactory.select(event.id).distinct()
                .from(event)
                .innerJoin(eventLike).on(eventLike.event.eq(event).and(eventLike.member.id.eq(memberId)))
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.isHidden.isFalse())
                                .and(createListFilterBuilder(searchTerm, regionIds, startDate, endDate, categoryCodes, type, truckTypes, isCatering, null, null))
                )
                .orderBy(getLikeOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(event.id.countDistinct()).from(event)
                .innerJoin(eventLike).on(eventLike.event.eq(event).and(eventLike.member.id.eq(memberId)))
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.isHidden.isFalse())
                                .and(createListFilterBuilder(searchTerm, regionIds, startDate, endDate, categoryCodes, type, truckTypes, isCatering, null, null))
                )
                .fetchOne();

        return new PageImpl<>(findEventsByIds(eventIds), pageable, total);
    }

    private BooleanBuilder createListFilterBuilder(String searchTerm, Map<RegionType, List<String>> regionIds,
                                                   LocalDate startDate, LocalDate endDate,
                                                   List<String> categoryCodes,
                                                   EventType type, Set<TruckType> truckTypes, Boolean isCatering,
                                                   LocalDate recruitEndDateFrom, LocalDate recruitEndDateTo) {
        BooleanBuilder builder = new BooleanBuilder();
        addSearchTermFilter(event, searchTerm, builder);
        addEventTypeFilter(type, builder);
        addTruckTypeFilter(truckTypes, builder);
        addCateringFilter(isCatering, builder);
        addRecruitEndDateFilter(recruitEndDateFrom, recruitEndDateTo, builder);

        BooleanExpression periodFilter = periodExistsCondition(startDate, endDate);
        if (periodFilter != null) {
            builder.and(periodFilter);
        }

        BooleanExpression categoryFilter = categoryExistsCondition(categoryCodes);
        if (categoryFilter != null) {
            builder.and(categoryFilter);
        }

        BooleanExpression regionFilter = regionExistsCondition(regionIds);
        if (regionFilter != null) {
            builder.and(regionFilter);
        }

        return builder;
    }

    private List<Event> findEventsByIds(List<String> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        List<Event> events = jpaQueryFactory.selectDistinct(event)
                .from(event)
                .innerJoin(event.recruitDetail, eventRecruitDetail).fetchJoin()
                .leftJoin(event.view, eventView).fetchJoin()
                .where(event.id.in(eventIds))
                .fetch();

        Map<String, Event> eventMap = new java.util.HashMap<>();
        for (Event eventEntity : events) {
            eventMap.put(eventEntity.getId(), eventEntity);
        }

        List<Event> orderedEvents = new ArrayList<>();
        for (String eventId : eventIds) {
            Event eventEntity = eventMap.get(eventId);
            if (eventEntity != null) {
                orderedEvents.add(eventEntity);
            }
        }

        return orderedEvents;
    }

    @Override
    public List<Event> findEndedEventsByIsSelecting(LocalDateTime now, Boolean isSelecting) {
        QEvent event = QEvent.event;
        QEventDate eventDate = QEventDate.eventDate;
        QEventRecruitDetail eventRecruitDetail = QEventRecruitDetail.eventRecruitDetail;

        return jpaQueryFactory.select(event)
                .from(event)
                .join(event.eventDates, eventDate)
                .join(event.recruitDetail, eventRecruitDetail)
                .where(event.isDeleted.isFalse()
                        .and(eventRecruitDetail.isSelecting.eq(isSelecting))
                )
                .groupBy(event.id)
                .having(eventDate.date.max().loe(now.toLocalDate()))
                .fetch();
    }

    public BooleanBuilder createFilterBuilder(String searchTerm, List<String> categoryCodes, LocalDate startDate, LocalDate endDate,
                                              EventType type, Set<TruckType> truckTypes, Boolean isCatering,
                                              LocalDate from, LocalDate to,
                                              QEvent event, QEventDate eventDate, QCategory category) {
        BooleanBuilder filterBuilder = new BooleanBuilder();
        addSearchTermFilter(event, searchTerm, filterBuilder);
        addCategoryFilter(category, categoryCodes, filterBuilder);
        addPeriodFilter(eventDate, startDate, endDate, filterBuilder);
        addTruckTypeFilter(truckTypes, filterBuilder);
        addEventTypeFilter(type, filterBuilder);
        addCateringFilter(isCatering, filterBuilder);
        addRecruitEndDateFilter(from, to, filterBuilder);
        return filterBuilder;
    }

    private void addRecruitEndDateFilter(LocalDate from, LocalDate to, BooleanBuilder builder) {
        if (from != null && to != null) {
            builder.and(eventRecruitDetail.recruitEndDateTime.between(
                    from.atStartOfDay(),
                    to.atTime(LocalTime.MAX)
            ));
        } else if (from != null) {
            builder.and(eventRecruitDetail.recruitEndDateTime.goe(from.atStartOfDay()));
        } else if (to != null) {
            builder.and(eventRecruitDetail.recruitEndDateTime.loe(to.atTime(LocalTime.MAX)));
        }
    }

    private void addEventTypeFilter(EventType type, BooleanBuilder builder) {
        if (type == null) {
            return;
        }
        builder.and(event.type.eq(type));
    }

    private void addTruckTypeFilter(Set<TruckType> types, BooleanBuilder builder) {
        if (types == null || types.isEmpty()) {
            return;
        }

        BooleanBuilder typeBuilder = new BooleanBuilder();

        // QTruck.truck.types 대신 DB 컬럼명을 stringPath로 사용
        StringPath typesPath = Expressions.stringPath("truckTypes"); // 실제 컬럼명 사용

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

    private BooleanExpression periodExistsCondition(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }

        QEventDate eventDateSub = new QEventDate("eventDateSub");
        return JPAExpressions.selectOne()
                .from(eventDateSub)
                .where(eventDateSub.event.eq(event)
                        .and(eventDateSub.date.between(startDate, endDate)))
                .exists();
    }

    private BooleanExpression categoryExistsCondition(List<String> categoryCodes) {
        if (categoryCodes == null || categoryCodes.isEmpty()) {
            return null;
        }

        QEventCategory eventCategorySub = new QEventCategory("eventCategorySub");
        QCategory categorySub = new QCategory("categorySub");

        return JPAExpressions.selectOne()
                .from(eventCategorySub)
                .innerJoin(eventCategorySub.category, categorySub)
                .where(eventCategorySub.event.eq(event)
                        .and(categorySub.code.in(categoryCodes)))
                .exists();
    }

    private BooleanExpression regionExistsCondition(Map<RegionType, List<String>> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            return null;
        }

        QEventRegion eventRegionSub = new QEventRegion("eventRegionSub");
        BooleanExpression regionCondition = regionFilterCondition(regionIds, eventRegionSub);
        if (regionCondition == null) {
            return null;
        }

        return JPAExpressions.selectOne()
                .from(eventRegionSub)
                .where(eventRegionSub.event.eq(event)
                        .and(regionCondition))
                .exists();
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

    private BooleanExpression regionFilterCondition(Map<RegionType, List<String>> regionIds, QEventRegion eventRegionAlias) {
        if (regionIds == null || regionIds.isEmpty()) {
            return null;
        }
        return eventRegionAlias.regionId.in(regionIds.get(RegionType.REGION_DO)).and(eventRegionAlias.regionType.eq(RegionType.REGION_DO))
                .or(eventRegionAlias.regionId.in(regionIds.get(RegionType.REGION_SI)).and(eventRegionAlias.regionType.eq(RegionType.REGION_SI)))
                .or(eventRegionAlias.regionId.in(regionIds.get(RegionType.REGION_GU)).and(eventRegionAlias.regionType.eq(RegionType.REGION_GU)))
                .or(eventRegionAlias.regionId.in(regionIds.get(RegionType.REGION_GUN)).and(eventRegionAlias.regionType.eq(RegionType.REGION_GUN)));
    }

    private void addCateringFilter(Boolean isCatering, BooleanBuilder builder) {
        if (isCatering == null) {
            return;
        }

        if (!isCatering) {//일반판매일 경우
            return;
        }
        builder.and(event.saleType.eq(SaleType.CATERING));
    }

    private List<OrderSpecifier> getOrderSpecifier(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            if (order.getProperty().equals("createdAt")) { //최신순
                orders.add(new OrderSpecifier(direction, event.createdAt));
            } else if (order.getProperty().equals("applicant")) { //지원순
                orders.add(new OrderSpecifier(direction, eventRecruitDetail.applicantCount));
            } else if (order.getProperty().equals("deadline")) { //마감순
                orders.add(new OrderSpecifier(direction, eventRecruitDetail.recruitEndDateTime));
            }
        });

        return orders;
    }

    private List<OrderSpecifier> getLikeOrderSpecifier(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            if (order.getProperty().equals("createdAt")) { //최신순
                orders.add(new OrderSpecifier(direction, event.createdAt));
            } else if (order.getProperty().equals("applicant")) { //지원순
                orders.add(new OrderSpecifier(direction, eventRecruitDetail.applicantCount));
            } else if (order.getProperty().equals("deadline")) { //마감순
                orders.add(new OrderSpecifier(direction, eventRecruitDetail.recruitEndDateTime));
            }
        });

        return orders;
    }

    @Override
    public Page<Event> findProgressEventManageList(String memberId, String status, Pageable pageable) {
        List<Event> events = jpaQueryFactory.selectDistinct(event)
                .from(event)
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
                .innerJoin(event.recruitDetail, eventRecruitDetail)
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
        List<String> eventIds = jpaQueryFactory
                .select(event.id)
                .distinct()
                .from(event)
                .innerJoin(event.eventRegion, eventRegion)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
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

        if (eventIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<Event> events = jpaQueryFactory
                .selectFrom(event)
                .innerJoin(event.eventRegion, eventRegion).fetchJoin()
                .innerJoin(event.recruitDetail, eventRecruitDetail).fetchJoin()
                .leftJoin(event.photos, eventPhoto).fetchJoin()
                .leftJoin(eventPhoto.file, file).fetchJoin()
                .where(event.id.in(eventIds))
                .orderBy(event.updatedAt.desc())
                .fetch();

        Long total = jpaQueryFactory.select(event.countDistinct()).from(event)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
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
                    //모집중이거나 모집 마감일 때
                    .and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITING)
                            .or(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITMENT_CLOSED)
                                    .and(eventRecruitDetail.isSelecting.isTrue()))
                    );
        }
        if (status.equals(EventRecruitingStatus.RECRUITING.toString())) {
            filterBuilder.and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITING));
        }
        if (status.equals(EventRecruitingStatus.RECRUITMENT_CLOSED.toString())) {
            filterBuilder.and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITMENT_CLOSED)
                    .and(eventRecruitDetail.isSelecting.isTrue()));
        }
        return filterBuilder;
    }

    public BooleanBuilder createCompletedFilterBuilder(String status) {
        BooleanBuilder filterBuilder = new BooleanBuilder();
        if (status.equals("ALL")) {
            filterBuilder.and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITMENT_CANCELLED)
                    .or(eventRecruitDetail.isSelecting.isFalse())
            );
        } else if (status.equals(EventRecruitingStatus.RECRUITMENT_CANCELLED.toString())) {
            filterBuilder.and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITMENT_CANCELLED));

        } else {
            filterBuilder.and(eventRecruitDetail.isSelecting.isFalse().and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITMENT_CLOSED)));
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

    @Override
    public List<Event> findAvailableEventListForProposal(String memberId) {
        return jpaQueryFactory.selectDistinct(event)
                .from(event)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .where(event.createdBy.eq(memberId)
                        .and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITING)))
                .orderBy(eventRecruitDetail.recruitEndDateTime.asc())
                .fetch();
    }

    @Override
    public List<MemberForEventFcmInfoDto> findSelectionNotEndedEventCreatorsFcmInfo() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return jpaQueryFactory
                .select(Projections.constructor(MemberForEventFcmInfoDto.class, event.id, event.name, member.id, member.fcmToken))
                .from(event)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .innerJoin(member).on(event.createdBy.eq(member.id))
                .leftJoin(event.eventDates, eventDate)
                .where(event.isDeleted.isFalse()
                        .and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITING))
                        .and(eventRecruitDetail.isSelecting.isTrue()).and(event.createdAt.lt(LocalDate.now().atStartOfDay())))
                .groupBy(event.id, event.name, member.id, member.fcmToken)
                .having(eventDate.date.min().eq(tomorrow))
                .fetch();
    }

    @Override
    public List<MemberForEventFcmInfoDto> findRecruitmentDeadlineSoonEventCreatorsFcmInfo() {
        LocalDateTime standardTime = LocalDateTime.now().withSecond(0).withNano(0).plusHours(6);
        return jpaQueryFactory
                .select(Projections.constructor(MemberForEventFcmInfoDto.class, event.id, event.name, member.id, member.fcmToken))
                .from(event)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .innerJoin(member).on(event.createdBy.eq(member.id))
                .where(event.isDeleted.isFalse().and(event.recruitDetail.isRecruitEndOnSelection.isFalse()).and(eventRecruitDetail.recruitEndDateTime.eq(standardTime)))
                .fetch();
    }

    @Override
    public List<Event> findRecruitmentDeadlineSoonEvents() {
        LocalDateTime standardTime = LocalDateTime.now().withSecond(0).withNano(0).plusHours(6);
        return jpaQueryFactory
                .selectFrom(event)
                .innerJoin(event.recruitDetail, eventRecruitDetail)
                .innerJoin(event.categories, eventCategory).fetchJoin()
                .innerJoin(eventCategory.category, category).fetchJoin()
                .where(event.isDeleted.isFalse().and(event.recruitDetail.isRecruitEndOnSelection.isFalse()).and(eventRecruitDetail.recruitEndDateTime.eq(standardTime)))
                .fetch();
    }

    @Override
    public Long findCountRecruitingStatus(String memberId) {
        return jpaQueryFactory.select(event.count())
                .from(event)
                .join(event.recruitDetail, eventRecruitDetail)
                .where(event.isDeleted.isFalse().and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITING)).and(event.createdBy.eq(memberId)))
                .fetchOne();
    }

    @Override
    public Long findCountProgressStatus(String memberId) {
        BooleanBuilder progressBuilder = new BooleanBuilder();
        //모집마감이면서
        progressBuilder.and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITMENT_CLOSED));

        //이벤트가 아직 종료되지 않음
        progressBuilder.and(
                event.id.in(
                        jpaQueryFactory
                                .select(event.id)
                                .from(event)
                                .leftJoin(event.eventDates, eventDate)
                                .groupBy(event.id)
                                .having(eventDate.date.max().goe(LocalDate.now()))
                )
        );

        return jpaQueryFactory.select(event.count())
                .from(event)
                .join(event.recruitDetail, eventRecruitDetail)
                .where(event.isDeleted.isFalse().and(progressBuilder).and(event.createdBy.eq(memberId)))
                .fetchOne();
    }

    @Override
    public Long findCountEndStatus(String memberId) {
        BooleanBuilder completedBuilder = new BooleanBuilder();
        //모집취소이거나
        completedBuilder.or(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITMENT_CANCELLED));

        //이벤트가 종료됨 (가장 마지막 이벤트 날짜 < 오늘)
        completedBuilder.or(
                event.id.in(
                        jpaQueryFactory
                                .select(event.id)
                                .from(event)
                                .leftJoin(event.eventDates, eventDate)
                                .groupBy(event.id)
                                .having(eventDate.date.max().before(LocalDate.now()))
                )
        );

        return jpaQueryFactory.select(event.count())
                .from(event)
                .join(event.recruitDetail, eventRecruitDetail)
                .where(event.isDeleted.isFalse().and(event.createdBy.eq(memberId)).and(completedBuilder))
                .fetchOne();
    }

    @Override
    public EventDashboardCount findEventDashboardCount(String memberId) {
        QEventDate progressEventDate = new QEventDate("progressEventDate");
        QEventDate completedFutureEventDate = new QEventDate("completedFutureEventDate");
        QEventDate completedPastEventDate = new QEventDate("completedPastEventDate");
        LocalDate today = LocalDate.now();

        Tuple counts = jpaQueryFactory
                .select(
                        new CaseBuilder()
                                .when(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITING))
                                .then(1L)
                                .otherwise(0L)
                                .sum(),
                        new CaseBuilder()
                                .when(
                                        eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITMENT_CLOSED)
                                                .and(
                                                        JPAExpressions.selectOne()
                                                                .from(progressEventDate)
                                                                .where(
                                                                        progressEventDate.event.eq(event),
                                                                        progressEventDate.date.goe(today)
                                                                )
                                                                .exists()
                                                )
                                )
                                .then(1L)
                                .otherwise(0L)
                                .sum(),
                        new CaseBuilder()
                                .when(
                                        eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITMENT_CANCELLED)
                                                .or(
                                                        JPAExpressions.selectOne()
                                                                .from(completedPastEventDate)
                                                                .where(
                                                                        completedPastEventDate.event.eq(event),
                                                                        completedPastEventDate.date.before(today)
                                                                )
                                                                .exists()
                                                                .and(
                                                                        JPAExpressions.selectOne()
                                                                                .from(completedFutureEventDate)
                                                                                .where(
                                                                                        completedFutureEventDate.event.eq(event),
                                                                                        completedFutureEventDate.date.goe(today)
                                                                                )
                                                                                .notExists()
                                                                )
                                                )
                                )
                                .then(1L)
                                .otherwise(0L)
                                .sum()
                )
                .from(event)
                .join(event.recruitDetail, eventRecruitDetail)
                .where(event.isDeleted.isFalse().and(event.createdBy.eq(memberId)))
                .fetchOne();

        return EventDashboardCount.builder()
                .recruitingCount(counts != null && counts.get(0, Long.class) != null ? counts.get(0, Long.class) : 0L)
                .progressCount(counts != null && counts.get(1, Long.class) != null ? counts.get(1, Long.class) : 0L)
                .endCount(counts != null && counts.get(2, Long.class) != null ? counts.get(2, Long.class) : 0L)
                .build();
    }

    public String getEventPhone(String eventId) {
        return jpaQueryFactory
                .select(event.contact)
                .from(event)
                .where(event.id.eq(eventId))
                .fetchFirst();
    }
}

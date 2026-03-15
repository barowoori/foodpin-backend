package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.QCategory;
import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.MemberForEventFcmInfoDto;
import com.barowoori.foodpinbackend.member.command.domain.model.EventCreatorType;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruck.truck;

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
                                             List<String> categoryCodes,
                                             EventType type, ExpectedParticipants expectedParticipants, Set<TruckType> truckTypes, Boolean isCatering,
                                             Pageable pageable) {
        List<Event> events = jpaQueryFactory.selectDistinct(event)
                .from(event)
                .leftJoin(event.eventRegion, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .innerJoin(event.recruitDetail, eventRecruitDetail).fetchJoin()
                .leftJoin(event.view, eventView)
                .leftJoin(event.photos, eventPhoto)
                .leftJoin(eventPhoto.file, file)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.isHidden.isFalse())
                                .and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITING))
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, type, expectedParticipants, truckTypes, isCatering, event, eventDate, category)
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
                                .and(event.isHidden.isFalse())
                                .and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITING))
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, type, expectedParticipants, truckTypes, isCatering, event, eventDate, category)
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .fetchOne();

        return new PageImpl<>(events, pageable, total);
    }

    @Override
    public Page<Event> findBackOfficeEventListByFilter(String searchTerm, Map<RegionType, List<String>> regionIds,
                                             LocalDate startDate, LocalDate endDate,
                                             List<String> categoryCodes,
                                             EventType type, ExpectedParticipants expectedParticipants, Set<TruckType> truckTypes, Boolean isCatering,
                                             Pageable pageable) {
        List<Event> events = jpaQueryFactory.selectDistinct(event)
                .from(event)
                .leftJoin(event.eventRegion, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .innerJoin(event.recruitDetail, eventRecruitDetail).fetchJoin()
                .leftJoin(event.view, eventView)
                .leftJoin(event.photos, eventPhoto)
                .leftJoin(eventPhoto.file, file)
                .where(
                        event.isDeleted.isFalse()
                                .and(event.creatorType.eq(EventCreatorType.ADMIN))
                                .and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITING))
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, type, expectedParticipants, truckTypes, isCatering, event, eventDate, category)
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
                                .and(event.creatorType.eq(EventCreatorType.ADMIN))
                                .and(eventRecruitDetail.recruitingStatus.eq(EventRecruitingStatus.RECRUITING))
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, type, expectedParticipants, truckTypes, isCatering, event, eventDate, category)
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .fetchOne();

        return new PageImpl<>(events, pageable, total);
    }

    @Override
    public Page<Event> findLikeEventListByFilter(String memberId, String searchTerm, Map<RegionType, List<String>> regionIds,
                                                 LocalDate startDate, LocalDate endDate,
                                                 List<String> categoryCodes,
                                                 EventType type, ExpectedParticipants expectedParticipants, Set<TruckType> truckTypes, Boolean isCatering,
                                                 Pageable pageable) {
        List<Event> events = jpaQueryFactory.selectDistinct(event)
                .from(event)
                .innerJoin(eventLike).on(eventLike.event.eq(event).and(eventLike.member.id.eq(memberId)))
                .leftJoin(event.eventRegion, eventRegion)
                .leftJoin(event.eventDates, eventDate)
                .leftJoin(event.categories, eventCategory)
                .leftJoin(eventCategory.category, category)
                .innerJoin(event.recruitDetail, eventRecruitDetail).fetchJoin()
                .leftJoin(event.view, eventView)
                .leftJoin(event.photos, eventPhoto)
                .leftJoin(eventPhoto.file, file)
                .where(
                        event.isDeleted.isFalse()
                                .and(
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, type, expectedParticipants, truckTypes, isCatering, event, eventDate, category)
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .orderBy(getLikeOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
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
                                        createFilterBuilder(searchTerm, categoryCodes, startDate, endDate, type, expectedParticipants, truckTypes, isCatering, event, eventDate, category)
                                                .and(regionFilterCondition(regionIds))
                                )
                )
                .fetchOne();

        return new PageImpl<>(events, pageable, total);
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
                                              EventType type, ExpectedParticipants expectedParticipants, Set<TruckType> truckTypes, Boolean isCatering,
                                              QEvent event, QEventDate eventDate, QCategory category) {
        BooleanBuilder filterBuilder = new BooleanBuilder();
        addSearchTermFilter(event, searchTerm, filterBuilder);
        addCategoryFilter(category, categoryCodes, filterBuilder);
        addPeriodFilter(eventDate, startDate, endDate, filterBuilder);
        addTruckTypeFilter(truckTypes, filterBuilder);
        addEventTypeFilter(type, filterBuilder);
        addExpectedParticipantsFilter(expectedParticipants, filterBuilder);
        addCateringFilter(isCatering, filterBuilder);
        return filterBuilder;
    }

    private void addEventTypeFilter(EventType type, BooleanBuilder builder) {
        if (type == null) {
            return;
        }
        builder.and(event.type.eq(type));
    }

    private void addExpectedParticipantsFilter(ExpectedParticipants expectedParticipants, BooleanBuilder builder) {
        if (expectedParticipants == null) {
            return;
        }
        builder.and(event.expectedParticipants.eq(expectedParticipants));
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

    private BooleanExpression regionFilterCondition(Map<RegionType, List<String>> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            return null;
        }
        return eventRegion.regionId.in(regionIds.get(RegionType.REGION_DO)).and(eventRegion.regionType.eq(RegionType.REGION_DO))
                .or(eventRegion.regionId.in(regionIds.get(RegionType.REGION_SI)).and(eventRegion.regionType.eq(RegionType.REGION_SI)))
                .or(eventRegion.regionId.in(regionIds.get(RegionType.REGION_GU)).and(eventRegion.regionType.eq(RegionType.REGION_GU)))
                .or(eventRegion.regionId.in(regionIds.get(RegionType.REGION_GUN)).and(eventRegion.regionType.eq(RegionType.REGION_GUN)));
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
                .where(event.isDeleted.isFalse().and(eventRecruitDetail.isSelecting.isTrue()).and(event.createdAt.lt(LocalDate.now().atStartOfDay())))
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

    public String getEventPhone(String eventId) {
        return jpaQueryFactory
                .select(event.contact)
                .from(event)
                .where(event.id.eq(eventId))
                .fetchFirst();
    }
}

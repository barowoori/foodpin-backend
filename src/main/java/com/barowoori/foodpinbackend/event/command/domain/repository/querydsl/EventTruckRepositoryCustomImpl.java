package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.QCategory;
import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventTruckManagerFcmInfoDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.barowoori.foodpinbackend.event.command.domain.model.EventTruckStatus.CONFIRMED;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEvent.event;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventApplication.eventApplication;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventPhoto.eventPhoto;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventRecruitDetail.eventRecruitDetail;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventTruck.eventTruck;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventTruckDate.eventTruckDate;
import static com.barowoori.foodpinbackend.file.command.domain.model.QFile.file;
import static com.barowoori.foodpinbackend.member.command.domain.model.QMember.member;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruck.truck;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckDocument.truckDocument;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckManager.truckManager;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenu.truckMenu;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckPhoto.truckPhoto;

public class EventTruckRepositoryCustomImpl implements EventTruckRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public EventTruckRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<EventTruck> findSelectedEventTrucks(String eventId, String status, Pageable pageable) {
        List<EventTruck> eventTrucks = jpaQueryFactory.selectFrom(eventTruck)
                .innerJoin(eventTruck.truck, truck)
                .leftJoin(truck.menus, truckMenu)
                .leftJoin(eventTruck.dates, eventTruckDate)
                .leftJoin(truck.documents, truckDocument)
                .leftJoin(truck.photos, truckPhoto)
                .leftJoin(truckPhoto.file, file)
                .where(eventTruck.event.id.eq(eventId)
                        .and(createFilterBuilder(status)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(
                        new CaseBuilder()
                                .when(eventTruck.status.eq(EventTruckStatus.PENDING)).then(1)
                                .when(eventTruck.status.eq(CONFIRMED)).then(2)
                                .when(eventTruck.status.eq(EventTruckStatus.REJECTED)).then(3)
                                .otherwise(4)
                                .asc()
                )
                .fetch();

        Long total = jpaQueryFactory.select(eventTruck.count()).from(eventTruck)
                .where(eventTruck.event.id.eq(eventId)
                        .and(createFilterBuilder(status)))
                .fetchOne();

        return new PageImpl<>(eventTrucks, pageable, total);
    }

    public BooleanBuilder createFilterBuilder(String status) {
        BooleanBuilder filterBuilder = new BooleanBuilder();
        if (status.equals("ALL")) {
            return filterBuilder;
        }
        filterBuilder.and(eventTruck.status.eq(EventTruckStatus.valueOf(status)));
        return filterBuilder;
    }

    @Override
    public Page<EventTruck> findSelectedApplications(String status, String truckId, Pageable pageable) {
        List<EventTruck> eventTrucks = jpaQueryFactory.selectDistinct(eventTruck)
                .from(eventTruck)
                .innerJoin(eventTruck.truck, truck)
                .leftJoin(eventTruck.dates, eventTruckDate)
                .innerJoin(eventTruck.event, event)
                .leftJoin(event.recruitDetail, eventRecruitDetail)
                .leftJoin(event.photos, eventPhoto)
                .leftJoin(eventPhoto.file, file)
                .where(truck.id.eq(truckId)
                        .and(createStatusBuilder(status)))
                .orderBy(eventTruck.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(eventTruck.countDistinct()).from(eventTruck)
                .innerJoin(eventTruck.truck, truck)
                .leftJoin(eventTruck.dates, eventTruckDate)
                .innerJoin(eventTruck.event, event)
                .leftJoin(event.recruitDetail, eventRecruitDetail)
                .where(truck.id.eq(truckId)
                        .and(createStatusBuilder(status)))
                .fetchOne();

        return new PageImpl<>(eventTrucks, pageable, total);
    }

    private BooleanBuilder createStatusBuilder(String status) {
        BooleanBuilder filterBuilder = new BooleanBuilder();
        if (status.equals("ALL")) {
            return filterBuilder;
        }
        if (EventTruckStatus.PENDING.toString().equals(status)) {
            return filterBuilder.and(eventTruck.status.eq(EventTruckStatus.PENDING));
        }
        if (EventTruckStatus.REJECTED.toString().equals(status)) {
            return filterBuilder.and(eventTruck.status.eq(EventTruckStatus.REJECTED));
        }
        if (status.equals("CONFIRMED")) {
            return filterBuilder.and(eventTruck.status.eq(EventTruckStatus.CONFIRMED));
        }
        if (status.equals("COMPLETED")) {
            return filterBuilder.and(eventTruck.status.eq(EventTruckStatus.CONFIRMED).and(eventRecruitDetail.recruitEndDateTime.before(LocalDateTime.now())));
        }
        return filterBuilder;
    }

    @Override
    public Boolean isConfirmedEventTruck(String eventId, String truckId) {
        return jpaQueryFactory.selectFrom(eventTruck)
                .innerJoin(eventTruck.event, event).on(event.id.eq(eventId))
                .innerJoin(eventTruck.truck, truck).on(truck.id.eq(truckId))
                .where(eventTruck.status.eq(CONFIRMED))
                .fetchFirst() != null;
    }

    public EventTruck findConfirmedEventTruck(String eventId, String truckId) {
        return jpaQueryFactory.selectFrom(eventTruck)
                .innerJoin(eventTruck.event, event).on(event.id.eq(eventId))
                .innerJoin(eventTruck.truck, truck).on(truck.id.eq(truckId))
                .where(eventTruck.status.eq(CONFIRMED))
                .fetchOne();
    }

    @Override
    public List<MemberFcmInfoDto> findEventTruckManagersFcmInfo(String eventTruckId) {
        return jpaQueryFactory
                .selectDistinct(Projections.constructor(MemberFcmInfoDto.class, member.id, member.fcmToken))
                .from(eventTruck)
                .innerJoin(eventTruck.truck, truck)
                .innerJoin(truckManager).on(truck.eq(truckManager.truck))
                .innerJoin(truckManager.member, member)
                .where(eventTruck.id.eq(eventTruckId))
                .fetch();
    }

    @Override
    public List<MemberFcmInfoDto> findConfirmedEventTruckManagersFcmInfo(String eventId) {
        return jpaQueryFactory
                .selectDistinct(Projections.constructor(MemberFcmInfoDto.class, member.id, member.fcmToken))
                .from(event)
                .innerJoin(eventTruck).on(eventTruck.event.eq(event))
                .innerJoin(eventTruck.truck, truck)
                .innerJoin(truckManager).on(truck.eq(truckManager.truck))
                .innerJoin(truckManager.member, member)
                .where(event.id.eq(eventId).and(eventTruck.status.eq(CONFIRMED)))
                .fetch();
    }

    @Override
    public List<EventTruckManagerFcmInfoDto> findPendingEventTruckManagersFcmInfo() {
        LocalDateTime now = LocalDateTime.now();

        return jpaQueryFactory
                .selectDistinct(Projections.constructor(EventTruckManagerFcmInfoDto.class,event.id, event.name, member.id, member.fcmToken))
                .from(eventTruck)
                .innerJoin(eventTruck.event, event)
                .innerJoin(eventTruck.truck, truck)
                .innerJoin(truckManager).on(truck.eq(truckManager.truck))
                .innerJoin(truckManager.member, member)
                .where(
                        eventTruck.status.eq(EventTruckStatus.PENDING),
                        eventTruck.createdAt.isNotNull(),
                        eventTruck.createdAt.loe(now.minusHours(24)),
                        Expressions.numberTemplate(
                                Long.class,
                                "MOD(TIMESTAMPDIFF(HOUR, {0}, {1}), 24)",
                                eventTruck.createdAt, now
                        ).eq(0L)
                )
                .fetch();
    }
}

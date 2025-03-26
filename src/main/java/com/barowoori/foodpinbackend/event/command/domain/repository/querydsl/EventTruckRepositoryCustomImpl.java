package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.QCategory;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static com.barowoori.foodpinbackend.event.command.domain.model.QEvent.event;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventApplication.eventApplication;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventApplicationDate.eventApplicationDate;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventPhoto.eventPhoto;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventTruck.eventTruck;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventTruckDate.eventTruckDate;
import static com.barowoori.foodpinbackend.file.command.domain.model.QFile.file;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruck.truck;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckDocument.truckDocument;
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
                                .when(eventTruck.status.eq(EventTruckStatus.CONFIRMED)).then(2)
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
                .leftJoin(event.photos, eventPhoto)
                .leftJoin(eventPhoto.file, file)
                .where(truck.id.eq(truckId)
                        .and(createStatusBuilder(status)))
                .orderBy(eventApplication.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(eventTruck.countDistinct()).from(eventTruck)
                .innerJoin(eventTruck.truck, truck)
                .leftJoin(eventTruck.dates, eventTruckDate)
                .innerJoin(eventTruck.event, event)
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
        if (EventTruckStatus.CONFIRMED.toString().equals(status)) {
            return filterBuilder.and(eventTruck.status.eq(EventTruckStatus.CONFIRMED));
        }

        if (EventStatus.COMPLETED.toString().equals(status)) {
            return filterBuilder.and(eventTruck.status.eq(EventTruckStatus.CONFIRMED).and(eventTruck.event.status.eq(EventStatus.COMPLETED)));
        }
        return filterBuilder;
    }

}

package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;


import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplicationStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.barowoori.foodpinbackend.event.command.domain.model.QEventApplication.eventApplication;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventApplicationDate.eventApplicationDate;
import static com.barowoori.foodpinbackend.file.command.domain.model.QFile.file;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruck.truck;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckDocument.truckDocument;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckMenu.truckMenu;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckPhoto.truckPhoto;

public class EventApplicationRepositoryCustomImpl implements EventApplicationRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public EventApplicationRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<EventApplication> findPendingEventApplications(String eventId, Pageable pageable) {
        List<EventApplication> eventApplications = jpaQueryFactory.selectFrom(eventApplication)
                .innerJoin(eventApplication.truck, truck)
                .leftJoin(truck.menus, truckMenu)
                .leftJoin(eventApplication.dates, eventApplicationDate)
                .leftJoin(truck.photos, truckPhoto)
                .leftJoin(truck.documents, truckDocument)
                .leftJoin(truckPhoto.file, file)
                .where(eventApplication.event.id.eq(eventId)
                        .and(eventApplication.status.eq(EventApplicationStatus.PENDING)))
                .orderBy(eventApplication.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(eventApplication.count())
                .from(eventApplication)
                .where(eventApplication.event.id.eq(eventId)
                        .and(eventApplication.status.eq(EventApplicationStatus.PENDING)))
                .fetchOne();

        return new PageImpl<>(eventApplications, pageable, total);
    }

    @Override
    public Page<EventApplication> findRejectedEventApplications(String eventId, Pageable pageable) {
        List<EventApplication> eventApplications = jpaQueryFactory.selectFrom(eventApplication)
                .innerJoin(eventApplication.truck, truck)
                .leftJoin(truck.menus, truckMenu)
                .leftJoin(eventApplication.dates, eventApplicationDate)
                .leftJoin(truck.documents, truckDocument)
                .leftJoin(truck.photos, truckPhoto)
                .leftJoin(truckPhoto.file, file)
                .where(eventApplication.event.id.eq(eventId)
                        .and(eventApplication.status.eq(EventApplicationStatus.REJECTED)))
                .orderBy(eventApplication.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(eventApplication.count())
                .from(eventApplication)
                .where(eventApplication.event.id.eq(eventId)
                        .and(eventApplication.status.eq(EventApplicationStatus.REJECTED)))
                .fetchOne();

        return new PageImpl<>(eventApplications, pageable, total);
    }
}

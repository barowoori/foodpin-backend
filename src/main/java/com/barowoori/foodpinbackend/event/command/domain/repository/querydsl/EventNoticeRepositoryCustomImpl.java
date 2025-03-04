package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.event.command.domain.model.EventNotice;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.barowoori.foodpinbackend.event.command.domain.model.QEventNotice.eventNotice;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventNoticeView.eventNoticeView;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventTruck.eventTruck;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruck.truck;

public class EventNoticeRepositoryCustomImpl implements EventNoticeRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public EventNoticeRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<EventNotice> findEventNoticeListByEventId(String eventId, Pageable pageable) {
        List<EventNotice> eventNotices = jpaQueryFactory.selectFrom(eventNotice)
                .where(eventNotice.event.id.eq(eventId).and(eventNotice.isDeleted.isFalse()))
                .orderBy(eventNotice.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(eventNotice.count()).from(eventNotice)
                .where(eventNotice.event.id.eq(eventId).and(eventNotice.isDeleted.isFalse()))
                .fetchOne();

        return new PageImpl<>(eventNotices, pageable, total);
    }

    @Override
    public EventNotice findEventNoticeForCreator(String eventNoticeId) {
        return jpaQueryFactory.selectFrom(eventNotice)
                .leftJoin(eventNotice.views, eventNoticeView).fetchJoin()
                .leftJoin(eventNoticeView.eventTruck, eventTruck)
                .leftJoin(eventTruck.truck, truck)
                .where(eventNotice.id.eq(eventNoticeId))
                .fetchOne();
    }

}

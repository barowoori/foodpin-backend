package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static com.barowoori.foodpinbackend.event.command.domain.model.QEvent.event;
import static com.barowoori.foodpinbackend.event.command.domain.model.QEventProposal.eventProposal;

public class EventProposalRepositoryCustomImpl implements EventProposalRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public EventProposalRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<String> findEventIdsProposedToTruckByMember(String truckId, String memberId) {
        return jpaQueryFactory
                .select(eventProposal.event.id)
                .from(eventProposal)
                .where(eventProposal.truck.id.eq(truckId).and(event.createdBy.eq(memberId)))
                .fetch();
    }

}

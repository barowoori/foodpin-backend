package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckManagerSummary;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.barowoori.foodpinbackend.member.command.domain.model.QMember.member;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckManager.truckManager;
import static com.querydsl.jpa.JPAExpressions.select;

public class TruckManagerRepositoryCustomImpl implements TruckManagerRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    public TruckManagerRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<TruckManagerSummary> findTruckManagerPages(String truckId, String memberId, Pageable pageable){
        List<TruckManagerSummary> truckManagers = jpaQueryFactory.select(Projections.fields(TruckManagerSummary.class,
                    truckManager.id.as("truckManagerId"),
                    member.nickname,
                    member.phone,
                    member.image,
                    truckManager.role.as("role")
                ))
                .from(truckManager)
                .innerJoin(truckManager.member, member)
                .where(truckManager.truck.id.eq(truckId))
                .orderBy(new CaseBuilder()
                        .when(truckManager.member.id.eq(memberId)).then(0)
                        .otherwise(1)
                        .asc(), truckManager.createAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(truckManager.count())
                .from(truckManager)
                .innerJoin(truckManager.member, member)
                .where(truckManager.truck.id.eq(truckId))
                .fetchOne();

        return new PageImpl<>(truckManagers, pageable, total);
    }
}

package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckManagerSummary;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.barowoori.foodpinbackend.file.command.domain.model.QFile.file;
import static com.barowoori.foodpinbackend.member.command.domain.model.QMember.member;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruck.truck;
import static com.barowoori.foodpinbackend.truck.command.domain.model.QTruckManager.truckManager;

public class TruckManagerRepositoryCustomImpl implements TruckManagerRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public TruckManagerRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<TruckManagerSummary> findTruckManagerPages(String truckId, String memberId, Pageable pageable) {
        List<TruckManagerSummary> truckManagers = jpaQueryFactory.select(Projections.fields(TruckManagerSummary.class,
                        truckManager.id.as("truckManagerId"),
                        member.nickname,
                        member.phone,
                        file.path.as("image"),
                        truckManager.role.as("role")
                ))
                .from(truckManager)
                .innerJoin(truckManager.member, member)
                .leftJoin(member.image, file)
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

    @Override
    public List<Truck> findOwnedTrucks(String memberId) {
        return jpaQueryFactory.select(truck)
                .from(truckManager)
                .innerJoin(truckManager.truck, truck)
                .innerJoin(truckManager.member, member)
                .where(member.id.eq(memberId)
                        .and(truck.isDeleted.isFalse()))
                .orderBy(truck.createdAt.desc())
                .fetch();

    }

    @Override
    public List<MemberFcmInfoDto> findTruckManagersFcmInfo(String truckId) {
        return jpaQueryFactory
                .select(Projections.constructor(MemberFcmInfoDto.class, member.id, member.fcmToken))
                .from(truckManager)
                .innerJoin(truckManager.member, member)
                .where(truckManager.truck.id.eq(truckId))
                .fetch();
    }
}

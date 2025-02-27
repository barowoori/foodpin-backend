package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class TruckRegionRepositoryCustomImpl implements TruckRegionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public TruckRegionRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

}

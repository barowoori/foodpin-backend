package com.barowoori.foodpinbackend.member.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRegion;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.barowoori.foodpinbackend.member.command.domain.model.QInterestEvent.interestEvent;
import static com.barowoori.foodpinbackend.member.command.domain.model.QInterestEventCategory.interestEventCategory;
import static com.barowoori.foodpinbackend.member.command.domain.model.QInterestEventRegion.interestEventRegion;
import static com.barowoori.foodpinbackend.member.command.domain.model.QMember.member;

@Repository
public class InterestEventRepositoryCustomImpl implements InterestEventRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public InterestEventRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<MemberFcmInfoDto> findInterestEventMemberFcmInfo(EventRegion eventRegion, List<Category> categories) {
        return jpaQueryFactory
                .selectDistinct(
                        Projections.constructor(
                                MemberFcmInfoDto.class,
                                member.id,
                                member.fcmToken
                        )
                )
                .from(interestEvent)
                .join(interestEvent.member, member)
                .where(notifyCondition(eventRegion, categories))
                .fetch();
    }

    private BooleanExpression notifyCondition(EventRegion eventRegion, List<Category> categories) {
        // 지역이 있는지
        BooleanExpression hasRegion =
                JPAExpressions
                        .selectOne()
                        .from(interestEventRegion)
                        .where(interestEventRegion.interestEvent.eq(interestEvent))
                        .exists();

// 카테고리가 있는지
        BooleanExpression hasCategory =
                JPAExpressions
                        .selectOne()
                        .from(interestEventCategory)
                        .where(interestEventCategory.interestEvent.eq(interestEvent))
                        .exists();

// 지역이 매칭되는지
        BooleanExpression regionMatch =
                JPAExpressions
                        .selectOne()
                        .from(interestEventRegion)
                        .where(
                                interestEventRegion.interestEvent.eq(interestEvent),
                                interestEventRegion.regionType.eq(eventRegion.getRegionType()),
                                interestEventRegion.regionId.in(eventRegion.getRegionId())
                        )
                        .exists();

// 카테고리가 매칭되는지
        BooleanExpression categoryMatch =
                JPAExpressions
                        .selectOne()
                        .from(interestEventCategory)
                        .where(
                                interestEventCategory.interestEvent.eq(interestEvent),
                                interestEventCategory.category.in(categories)
                        )
                        .exists();

        return
                // 1. 지역 O, 카테고리 O
                hasRegion.and(hasCategory)
                        .and(regionMatch)
                        .and(categoryMatch)

                        // 2. 지역 X, 카테고리 O
                        .or(hasRegion.not()
                                .and(hasCategory)
                                .and(categoryMatch))

                        // 3. 지역 O, 카테고리 X
                        .or(hasRegion
                                .and(hasCategory.not())
                                .and(regionMatch));
    }
}

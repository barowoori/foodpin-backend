package com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationTargetType;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationType;
import com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.model.PushAlarmHistory;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.barowoori.foodpinbackend.member.command.domain.model.QMember.member;
import static com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.model.QPushAlarmHistory.pushAlarmHistory;

public class PushAlarmHistoryRepositoryCustomImpl implements PushAlarmHistoryRepositoryCustom {
    
    private final JPAQueryFactory jpaQueryFactory;

    public PushAlarmHistoryRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<PushAlarmHistory> findPushAlarmHistoryByMemberId(String memberId, Pageable pageable) {
        List<PushAlarmHistory> histories = jpaQueryFactory
                .selectFrom(pushAlarmHistory)
                .leftJoin(pushAlarmHistory.member, member).fetchJoin()
                .where(pushAlarmHistory.member.id.eq(memberId))
                .orderBy(pushAlarmHistory.createAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(pushAlarmHistory.count())
                .from(pushAlarmHistory)
                .where(pushAlarmHistory.member.id.eq(memberId))
                .fetchOne();

        return new PageImpl<>(histories, pageable, total != null ? total : 0L);
    }

}

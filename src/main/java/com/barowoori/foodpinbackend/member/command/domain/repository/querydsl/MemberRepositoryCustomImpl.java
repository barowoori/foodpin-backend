package com.barowoori.foodpinbackend.member.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


import static com.barowoori.foodpinbackend.member.command.domain.model.QMember.member;

@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public MemberRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public MemberFcmInfoDto findMemberFcmInfo(String memberId) {
        return jpaQueryFactory
                .select(Projections.constructor(MemberFcmInfoDto.class, member.id, member.fcmToken))
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();
    }

    @Override
    public Page<Member> findBackOfficeMemberList(String search, Boolean isDeleted, Pageable pageable) {
        List<Member> content = jpaQueryFactory
                .selectFrom(member)
                .where(
                        excludeUnregistered(),
                        searchContains(search),
                        isDeletedEq(isDeleted)
                )
                .orderBy(member.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(
                        excludeUnregistered(),
                        searchContains(search),
                        isDeletedEq(isDeleted)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<Member> searchBackOfficeMembersForTruckManager(String search, Pageable pageable) {
        List<Member> content = jpaQueryFactory
                .selectFrom(member)
                .where(
                        excludeUnregistered(),
                        member.isDeleted.isFalse(),
                        nicknameOrPhoneContains(search)
                )
                .orderBy(member.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(
                        excludeUnregistered(),
                        member.isDeleted.isFalse(),
                        nicknameOrPhoneContains(search)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanExpression searchContains(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }

        return member.nickname.contains(search)
                .or(member.phone.contains(search))
                .or(member.email.contains(search));
    }

    private BooleanExpression isDeletedEq(Boolean isDeleted) {
        if (isDeleted == null) {
            return null;
        }
        return member.isDeleted.eq(isDeleted);
    }

    private BooleanExpression nicknameOrPhoneContains(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }

        return member.nickname.contains(search)
                .or(member.phone.contains(search));
    }

    private BooleanExpression excludeUnregistered() {
        return member.socialLoginInfo.type.ne(SocialLoginType.UNREGISTERED)
                .or(member.socialLoginInfo.type.isNull());
    }
}

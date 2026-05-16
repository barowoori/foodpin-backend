package com.barowoori.foodpinbackend.member.command.domain.repository;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.querydsl.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MemberRepository extends JpaRepository<Member, String>, MemberRepositoryCustom {
    Member findBySocialLoginInfo_TypeAndSocialLoginInfo_Id(SocialLoginType type, String id);
    Member findByNickname(String nickname);
    Member findByPhone(String phone);
    long countByCreatedAtBetweenAndSocialLoginInfo_Type(LocalDateTime start, LocalDateTime end, SocialLoginType type);
    long countByCreatedAtBetweenAndSocialLoginInfo_TypeNot(LocalDateTime start, LocalDateTime end, SocialLoginType type);
}

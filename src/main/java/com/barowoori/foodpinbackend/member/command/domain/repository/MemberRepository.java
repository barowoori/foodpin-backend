package com.barowoori.foodpinbackend.member.command.domain.repository;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String>, MemberRepositoryCustom {
    Member findByNickname(String nickname);
    Member findByPhone(String phone);
}

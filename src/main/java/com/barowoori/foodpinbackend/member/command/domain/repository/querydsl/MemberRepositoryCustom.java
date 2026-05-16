package com.barowoori.foodpinbackend.member.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MemberRepositoryCustom {
    MemberFcmInfoDto findMemberFcmInfo(String memberId);
    Page<Member> findBackOfficeMemberList(String search, Boolean isDeleted, Pageable pageable);
    Page<Member> searchBackOfficeMembersForTruckManager(String search, Pageable pageable);
}

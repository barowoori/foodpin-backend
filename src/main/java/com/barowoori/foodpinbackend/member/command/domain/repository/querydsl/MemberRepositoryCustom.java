package com.barowoori.foodpinbackend.member.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;

public interface MemberRepositoryCustom {
    MemberFcmInfoDto findMemberFcmInfo(String memberId);
}

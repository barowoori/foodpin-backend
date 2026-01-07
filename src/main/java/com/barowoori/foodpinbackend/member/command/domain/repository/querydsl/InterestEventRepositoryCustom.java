package com.barowoori.foodpinbackend.member.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRegion;

import java.util.List;

public interface InterestEventRepositoryCustom {
    List<MemberFcmInfoDto> findInterestEventMemberFcmInfo(EventRegion eventRegion, List<Category> categories);
}

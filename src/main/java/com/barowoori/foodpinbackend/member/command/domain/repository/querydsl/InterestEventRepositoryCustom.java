package com.barowoori.foodpinbackend.member.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;

import java.util.List;
import java.util.Map;

public interface InterestEventRepositoryCustom {
    List<MemberFcmInfoDto> findInterestEventMemberFcmInfo(Map<RegionType, String> regionIds, List<Category> categories);
}

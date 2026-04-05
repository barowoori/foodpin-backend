package com.barowoori.foodpinbackend.region.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.region.command.domain.model.Region;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;

import java.util.List;
import java.util.Map;


public interface RegionRepositoryCustom {
    RegionInfo findByCode(String code);
    Map<RegionType, List<String>> findRegionIdsByFilter(List<String> filters);
    Map<RegionType, String> extractParentRegions(Region region);
    Region findRegionByCode(String code);
}

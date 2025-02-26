package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;

import java.util.List;
import java.util.Map;

public interface TruckRegionRepositoryCustom {
    List<String> findRegionNamesByTruckId(String truckId);
    List<RegionCode> findRegionCodesByTruckId(String truckId);
    Map<String, List<String>> findRegionNamesByTruckIds(List<String> truckIds);
}

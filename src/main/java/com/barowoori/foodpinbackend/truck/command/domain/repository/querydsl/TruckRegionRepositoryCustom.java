package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import java.util.List;
import java.util.Map;

public interface TruckRegionRepositoryCustom {
    List<String> findRegionNamesByTruckId(String truckId);
    Map<String, List<String>> findRegionNamesByTruckIds(List<String> truckIds);
}

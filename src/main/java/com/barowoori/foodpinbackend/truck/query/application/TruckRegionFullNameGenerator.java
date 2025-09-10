package com.barowoori.foodpinbackend.truck.query.application;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.region.command.domain.query.application.RegionFullNameGenerator;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckRegion;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TruckRegionFullNameGenerator {
    private final JPAQueryFactory jpaQueryFactory;
    private final RegionFullNameGenerator regionFullNameGenerator;

    public TruckRegionFullNameGenerator(JPAQueryFactory jpaQueryFactory, RegionFullNameGenerator regionFullNameGenerator) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.regionFullNameGenerator = regionFullNameGenerator;
    }

    public Map<String, List<String>> findRegionNamesByTruckIds(List<String> truckIds){
        Map<String, List<String>> result = new HashMap<>();
        for (String truckId : truckIds){
            result.put(truckId, findRegionNamesByTruckId(truckId));
        }
        return result;
    }

    public List<String> findRegionNamesByTruckId(String truckId) {
        QTruckRegion truckRegion = QTruckRegion.truckRegion;

        List<Tuple> results = jpaQueryFactory
                .select(truckRegion.regionType, truckRegion.regionId)
                .from(truckRegion)
                .where(truckRegion.truck.id.eq(truckId))
                .orderBy(truckRegion.createAt.asc())
                .fetch();

        List<String> regionNames = new ArrayList<>();

        for (Tuple result : results) {
            RegionType regionType = result.get(truckRegion.regionType);
            String regionId = result.get(truckRegion.regionId);
            String fullRegionName = regionFullNameGenerator.findFullRegionName(regionType, regionId);
            regionNames.add(fullRegionName);
        }

        return regionNames;
    }

    public List<RegionCode> findRegionCodesByTruckId(String truckId) {
        QTruckRegion truckRegion = QTruckRegion.truckRegion;

        List<Tuple> results = jpaQueryFactory
                .select(truckRegion.regionType, truckRegion.regionId)
                .from(truckRegion)
                .where(truckRegion.truck.id.eq(truckId))
                .orderBy(truckRegion.createAt.asc())
                .fetch();

        List<RegionCode> regionCodes = new ArrayList<>();

        for (Tuple result : results) {
            RegionType regionType = result.get(truckRegion.regionType);
            String regionId = result.get(truckRegion.regionId);
            String fullRegionName = regionFullNameGenerator.findFullRegionName(regionType, regionId);
            regionCodes.add(RegionCode.of(regionType.makeCode(regionId), fullRegionName));
        }

        return regionCodes;
    }

}

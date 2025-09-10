package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.event.command.domain.model.QEventRegion;
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
public class EventRegionFullNameGenerator {
    private final JPAQueryFactory jpaQueryFactory;
    private final RegionFullNameGenerator regionFullNameGenerator;

    public EventRegionFullNameGenerator(JPAQueryFactory jpaQueryFactory, RegionFullNameGenerator regionFullNameGenerator) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.regionFullNameGenerator = regionFullNameGenerator;
    }

    public Map<String, List<String>> findRegionNamesByEventIds(List<String> eventIds){
        Map<String, List<String>> result = new HashMap<>();
        for (String eventId : eventIds){
            result.put(eventId, findRegionNamesByEventId(eventId));
        }
        return result;
    }

    public List<String> findRegionNamesByEventId(String eventId) {
        QEventRegion eventRegion = QEventRegion.eventRegion;

        List<Tuple> results = jpaQueryFactory
                .select(eventRegion.regionType, eventRegion.regionId)
                .from(eventRegion)
                .where(eventRegion.event.id.eq(eventId))
                .fetch();

        List<String> regionNames = new ArrayList<>();

        for (Tuple result : results) {
            RegionType regionType = result.get(eventRegion.regionType);
            String regionId = result.get(eventRegion.regionId);
            String fullRegionName = regionFullNameGenerator.findFullRegionName(regionType, regionId);
            regionNames.add(fullRegionName);
        }

        return regionNames;
    }

    public List<RegionCode> findRegionCodesByEventId(String eventId) {
        QEventRegion eventRegion = QEventRegion.eventRegion;

        List<Tuple> results = jpaQueryFactory
                .select(eventRegion.regionType, eventRegion.regionId)
                .from(eventRegion)
                .where(eventRegion.event.id.eq(eventId))
                .fetch();

        List<RegionCode> regionCodes = new ArrayList<>();

        for (Tuple result : results) {
            RegionType regionType = result.get(eventRegion.regionType);
            String regionId = result.get(eventRegion.regionId);
            String fullRegionName = regionFullNameGenerator.findFullRegionName(regionType, regionId);
            regionCodes.add(RegionCode.of(regionType.makeCode(regionId), fullRegionName));
        }

        return regionCodes;
    }

    public String makeRegionList(List<RegionCode> regionCodes) {
        return regionFullNameGenerator.convertFormat(regionCodes);
    }

}

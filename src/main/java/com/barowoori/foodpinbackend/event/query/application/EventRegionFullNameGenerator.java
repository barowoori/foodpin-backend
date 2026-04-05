package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.event.command.domain.model.QEventRegion;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.region.command.domain.query.application.RegionFullNameGenerator;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionDo.regionDo;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionGu.regionGu;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionGun.regionGun;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionSi.regionSi;

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
        if (eventIds == null || eventIds.isEmpty()) {
            return result;
        }

        QEventRegion eventRegion = QEventRegion.eventRegion;
        List<Tuple> results = jpaQueryFactory
                .select(eventRegion.event.id, eventRegion.regionType, eventRegion.regionId, eventRegion.createdAt)
                .from(eventRegion)
                .where(eventRegion.event.id.in(eventIds))
                .orderBy(eventRegion.createdAt.asc())
                .fetch();

        Map<RegionType, List<String>> regionIdsByType = new EnumMap<>(RegionType.class);
        for (RegionType regionType : RegionType.values()) {
            regionIdsByType.put(regionType, new ArrayList<>());
        }

        for (String eventId : eventIds) {
            result.put(eventId, new ArrayList<>());
        }

        for (Tuple row : results) {
            RegionType regionType = row.get(eventRegion.regionType);
            String regionId = row.get(eventRegion.regionId);
            regionIdsByType.get(regionType).add(regionId);
        }

        Map<RegionType, Map<String, String>> fullNameMapByType = new EnumMap<>(RegionType.class);
        fullNameMapByType.put(RegionType.REGION_DO, findRegionDoNames(regionIdsByType.get(RegionType.REGION_DO)));
        fullNameMapByType.put(RegionType.REGION_SI, findRegionSiNames(regionIdsByType.get(RegionType.REGION_SI)));
        fullNameMapByType.put(RegionType.REGION_GU, findRegionGuNames(regionIdsByType.get(RegionType.REGION_GU)));
        fullNameMapByType.put(RegionType.REGION_GUN, findRegionGunNames(regionIdsByType.get(RegionType.REGION_GUN)));

        for (Tuple row : results) {
            String eventId = row.get(eventRegion.event.id);
            RegionType regionType = row.get(eventRegion.regionType);
            String regionId = row.get(eventRegion.regionId);
            String fullRegionName = fullNameMapByType.get(regionType).get(regionId);

            if (fullRegionName != null) {
                result.get(eventId).add(fullRegionName);
            }
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

    private Map<String, String> findRegionDoNames(List<String> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            return Map.of();
        }

        List<Tuple> results = jpaQueryFactory
                .select(regionDo.id, regionDo.name)
                .from(regionDo)
                .where(regionDo.id.in(regionIds))
                .fetch();

        Map<String, String> regionNames = new HashMap<>();
        for (Tuple row : results) {
            regionNames.put(row.get(regionDo.id), row.get(regionDo.name));
        }
        return regionNames;
    }

    private Map<String, String> findRegionSiNames(List<String> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            return Map.of();
        }

        List<Tuple> results = jpaQueryFactory
                .select(regionSi.id, regionSi.name, regionDo.name)
                .from(regionSi)
                .leftJoin(regionSi.regionDo, regionDo)
                .where(regionSi.id.in(regionIds))
                .fetch();

        Map<String, String> regionNames = new HashMap<>();
        for (Tuple row : results) {
            regionNames.put(row.get(regionSi.id), makeFullName(row.get(regionDo.name), row.get(regionSi.name), null, null));
        }
        return regionNames;
    }

    private Map<String, String> findRegionGuNames(List<String> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            return Map.of();
        }

        List<Tuple> results = jpaQueryFactory
                .select(regionGu.id, regionGu.name, regionSi.name, regionDo.name)
                .from(regionGu)
                .leftJoin(regionGu.regionSi, regionSi)
                .leftJoin(regionGu.regionDo, regionDo)
                .where(regionGu.id.in(regionIds))
                .fetch();

        Map<String, String> regionNames = new HashMap<>();
        for (Tuple row : results) {
            regionNames.put(row.get(regionGu.id), makeFullName(row.get(regionDo.name), row.get(regionSi.name), row.get(regionGu.name), null));
        }
        return regionNames;
    }

    private Map<String, String> findRegionGunNames(List<String> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            return Map.of();
        }

        List<Tuple> results = jpaQueryFactory
                .select(regionGun.id, regionGun.name, regionSi.name, regionDo.name)
                .from(regionGun)
                .leftJoin(regionGun.regionSi, regionSi)
                .leftJoin(regionGun.regionDo, regionDo)
                .where(regionGun.id.in(regionIds))
                .fetch();

        Map<String, String> regionNames = new HashMap<>();
        for (Tuple row : results) {
            regionNames.put(row.get(regionGun.id), makeFullName(row.get(regionDo.name), row.get(regionSi.name), null, row.get(regionGun.name)));
        }
        return regionNames;
    }

    private String makeFullName(String doName, String siName, String guName, String gunName) {
        List<String> parts = new ArrayList<>();

        if (doName != null) {
            parts.add(doName);
        }
        if (siName != null) {
            parts.add(siName);
        }
        if (guName != null) {
            parts.add(guName);
        }
        if (gunName != null) {
            parts.add(gunName);
        }

        return parts.isEmpty() ? null : String.join(" ", parts);
    }

}

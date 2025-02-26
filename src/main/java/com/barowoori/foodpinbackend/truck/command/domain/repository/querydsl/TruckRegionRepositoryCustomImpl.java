package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.region.command.domain.model.*;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckRegion;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionDo.regionDo;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionGu.regionGu;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionGun.regionGun;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionSi.regionSi;

public class TruckRegionRepositoryCustomImpl implements TruckRegionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public TruckRegionRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
    @Override
    public Map<String, List<String>> findRegionNamesByTruckIds(List<String> truckIds){
        Map<String, List<String>> result = new HashMap<>();
        for (String truckId : truckIds){
            result.put(truckId, findRegionNamesByTruckId(truckId));
        }
        return result;
    }

    @Override
    public List<String> findRegionNamesByTruckId(String truckId) {
        QTruckRegion truckRegion = QTruckRegion.truckRegion;

        List<Tuple> results = jpaQueryFactory
                .select(truckRegion.regionType, truckRegion.regionId)
                .from(truckRegion)
                .where(truckRegion.truck.id.eq(truckId))
                .fetch();

        List<String> regionNames = new ArrayList<>();

        for (Tuple result : results) {
            RegionType regionType = result.get(truckRegion.regionType);
            String regionId = result.get(truckRegion.regionId);
            String fullRegionName = findFullRegionName(regionType, regionId);
            regionNames.add(fullRegionName);
        }

        return regionNames;
    }

    @Override
    public List<RegionCode> findRegionCodesByTruckId(String truckId) {
        QTruckRegion truckRegion = QTruckRegion.truckRegion;

        List<Tuple> results = jpaQueryFactory
                .select(truckRegion.regionType, truckRegion.regionId)
                .from(truckRegion)
                .where(truckRegion.truck.id.eq(truckId))
                .fetch();

        List<RegionCode> regionCodes = new ArrayList<>();

        for (Tuple result : results) {
            RegionType regionType = result.get(truckRegion.regionType);
            String regionId = result.get(truckRegion.regionId);
            String fullRegionName = findFullRegionName(regionType, regionId);
            regionCodes.add(RegionCode.of(regionType.makeCode(regionId), fullRegionName));
        }

        return regionCodes;
    }

    private String findFullRegionName(RegionType regionType, String regionId) {
        QRegionDo regionDo = QRegionDo.regionDo;
        QRegionSi regionSi = QRegionSi.regionSi;
        QRegionGu regionGu = QRegionGu.regionGu;
        QRegionGun regionGun = QRegionGun.regionGun;

        if (regionType == RegionType.REGION_DO) {
            return jpaQueryFactory
                    .select(regionDo.name)
                    .from(regionDo)
                    .where(regionDo.id.eq(regionId))
                    .fetchOne();

        } else if (regionType == RegionType.REGION_SI) {
            Tuple result = jpaQueryFactory
                    .select(regionSi.name, regionDo.name)
                    .from(regionSi)
                    .leftJoin(regionDo).on(regionSi.regionDo.eq(regionDo))
                    .where(regionSi.id.eq(regionId))
                    .fetchOne();
            return makeFullName(result);

        } else if (regionType == RegionType.REGION_GU) {
            Tuple result = jpaQueryFactory
                    .select(regionGu.name, regionSi.name, regionDo.name)
                    .from(regionGu)
                    .leftJoin(regionSi).on(regionGu.regionSi.eq(regionSi))
                    .leftJoin(regionDo).on(regionSi.regionDo.eq(regionDo))
                    .where(regionGu.id.eq(regionId))
                    .fetchOne();
            return makeFullName(result);

        } else if (regionType == RegionType.REGION_GUN) {
            Tuple result = jpaQueryFactory
                    .select(regionGun.name, regionSi.name, regionDo.name)
                    .from(regionGun)
                    .leftJoin(regionSi).on(regionGun.regionSi.eq(regionSi))
                    .leftJoin(regionDo).on(regionGun.regionDo.eq(regionDo))
                    .where(regionGun.id.eq(regionId))
                    .fetchOne();
            return makeFullName(result);
        }
        return null;
    }

    private String makeFullName(Tuple result) {
        if (result == null) {
            return null;
        }

        StringBuilder fullName = new StringBuilder();

        if (result.get(regionDo.name) != null) {
            fullName.append(result.get(regionDo.name)).append(" ");
        }

        if (result.get(regionSi.name) != null) {
            fullName.append(result.get(regionSi.name)).append(" ");
        }

        if (result.get(regionGu.name) != null) {
            fullName.append(result.get(regionGu.name)).append(" ");
        }

        if (result.get(regionGun.name) != null) {
            fullName.append(result.get(regionGun.name));
        }

        String finalFullName = fullName.toString().trim();

        return finalFullName.isEmpty() ? null : finalFullName;
    }


}

package com.barowoori.foodpinbackend.region.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.region.command.domain.exception.RegionErrorCode;
import com.barowoori.foodpinbackend.region.command.domain.model.*;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegionRepositoryCustomImpl implements RegionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public RegionRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public RegionInfo findByCode(String code) {
        Map<String, String> codes = new HashMap<>();

        Pattern pattern = Pattern.compile("(SI|DO|GU|GUN)([a-fA-F0-9\\-]+)");
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            codes.put(matcher.group(1), matcher.group(2));
        }

        if (codes.containsKey("GUN") && isExistGUN(codes.get("GUN"))) {
            return RegionInfo.of(RegionType.REGION_GUN, codes.get("GUN"));
        } else if (codes.containsKey("GU") && isExistGU(codes.get("GU"))) {
            return RegionInfo.of(RegionType.REGION_GU, codes.get("GU"));
        } else if (codes.containsKey("SI") && isExistSI(codes.get("SI"))) {
            return RegionInfo.of(RegionType.REGION_SI, codes.get("SI"));
        } else if (codes.containsKey("DO") && isExistDO(codes.get("DO"))) {
            return RegionInfo.of(RegionType.REGION_DO, codes.get("DO"));
        } else {
            throw new CustomException(RegionErrorCode.NOT_CORRECT_REGION_CODE_PATTERN);
        }
    }


    private boolean isExistSI(String id) {
        QRegionSi regionSi = QRegionSi.regionSi;
        return jpaQueryFactory.selectFrom(regionSi).where(regionSi.id.eq(id)).fetchFirst() != null;
    }

    private boolean isExistDO(String id) {
        QRegionDo regionDo = QRegionDo.regionDo;
        return jpaQueryFactory.selectFrom(regionDo).where(regionDo.id.eq(id)).fetchFirst() != null;
    }

    private boolean isExistGU(String id) {
        QRegionGu regionGu = QRegionGu.regionGu;
        return jpaQueryFactory.selectFrom(regionGu).where(regionGu.id.eq(id)).fetchFirst() != null;
    }

    private boolean isExistGUN(String id) {
        QRegionGun regionGun = QRegionGun.regionGun;
        return jpaQueryFactory.selectFrom(regionGun).where(regionGun.id.eq(id)).fetchFirst() != null;
    }


    @Override
    public Map<RegionType, List<String>> findRegionIdsByFilter(List<String> filters) {
        if (filters == null){
            return  new HashMap<>();
        }
        Pattern pattern = Pattern.compile("(SI|DO|GU|GUN)([a-fA-F0-9\\-]+)");
        List<String> regionDoIds = new ArrayList<>();
        List<String> regionSiIds = new ArrayList<>();
        List<String> regionGuIds = new ArrayList<>();
        List<String> regionGunIds = new ArrayList<>();

        for (String filter : filters) {
            Map<String, String> codes = new HashMap<>();
            Matcher matcher = pattern.matcher(filter);

            while (matcher.find()) {
                codes.put(matcher.group(1), matcher.group(2));
            }
            if (codes.containsKey("GUN")) {
                regionGunIds.add(codes.get("GUN"));
            } else if (codes.containsKey("GU")) {
                regionGuIds.add(codes.get("GU"));
            } else if (codes.containsKey("SI")) {
                regionSiIds.add(codes.get("SI"));
            } else if (codes.containsKey("DO")) {
                regionDoIds.add(codes.get("DO"));
            }
        }
        List<String> filterDoIds = findDoByFilter(regionDoIds);
        List<String> filterSiIds = findSiByFilter(regionSiIds, filterDoIds);
        List<String> filterGuIds = findGuByFilter(regionGuIds, filterDoIds, filterSiIds);
        List<String> filterGunIds = findGunByFilter(regionGunIds, filterDoIds, filterSiIds, filterGuIds);

        Map<RegionType, List<String>> result = new HashMap<>();
        result.put(RegionType.REGION_DO, filterDoIds);
        result.put(RegionType.REGION_SI, filterSiIds);
        result.put(RegionType.REGION_GU, filterGuIds);
        result.put(RegionType.REGION_GUN, filterGunIds);

        return result;
    }

    private List<String> findDoByFilter(List<String> regionDoIds) {
        QRegionDo regionDo = QRegionDo.regionDo;
        return jpaQueryFactory.select(regionDo.id).from(regionDo)
                .where(regionDo.id.in(regionDoIds)).fetch();
    }

    private List<String> findSiByFilter(List<String> regionSiIds, List<String> regionDos) {
        QRegionSi regionSi = QRegionSi.regionSi;
        return jpaQueryFactory.select(regionSi.id).from(regionSi)
                .where(regionSi.id.in(regionSiIds)
                        .or(regionSi.regionDo.id.in(regionDos))
                ).fetch();
    }

    private List<String> findGuByFilter(List<String> regionGuIds, List<String> regionDos, List<String> regionSis) {
        QRegionGu regionGu = QRegionGu.regionGu;
        return jpaQueryFactory.select(regionGu.id).from(regionGu)
                .where(regionGu.id.in(regionGuIds)
                        .or(regionGu.regionDo.id.in(regionDos))
                        .or(regionGu.regionSi.id.in(regionSis))
                ).fetch();
    }

    private List<String> findGunByFilter(List<String> regionGunIds, List<String> regionDos, List<String> regionSis, List<String> regionGus) {
        QRegionGun regionGun = QRegionGun.regionGun;
        return jpaQueryFactory.select(regionGun.id).from(regionGun)
                .where(regionGun.id.in(regionGunIds)
                        .or(regionGun.regionDo.id.in(regionDos))
                        .or(regionGun.regionSi.id.in(regionSis))
                        .or(regionGun.regionGu.id.in(regionGus))
                ).fetch();
    }
}

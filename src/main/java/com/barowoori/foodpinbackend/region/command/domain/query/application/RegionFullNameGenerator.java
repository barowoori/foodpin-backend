package com.barowoori.foodpinbackend.region.command.domain.query.application;

import com.barowoori.foodpinbackend.region.command.domain.model.*;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionDo.regionDo;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionGu.regionGu;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionGun.regionGun;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionSi.regionSi;

@Component
public class RegionFullNameGenerator {
    private final JPAQueryFactory jpaQueryFactory;

    public RegionFullNameGenerator(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public String findFullRegionName(RegionType regionType, String regionId) {
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

    public String convertFormat(List<RegionCode> regionCodes) {
        Map<String, List<String>> grouped = regionCodes.stream()
                .collect(Collectors.groupingBy(
                        rc -> rc.getName().split(" ")[0], // 상위 지역
                        LinkedHashMap::new,               // 입력 순서 유지
                        Collectors.mapping(
                                rc -> rc.getName().substring(rc.getName().indexOf(" ") + 1), // 하위만 추출
                                Collectors.toList()
                        )
                ));

        // 문자열 합치기
        String result = grouped.entrySet().stream()
                .map(entry -> entry.getKey() + " " + String.join(", ", entry.getValue()))
                .collect(Collectors.joining(", "));

        return result;
    }

    public String convertFormatByRegionName(List<String> regionNames) {
        Map<String, List<String>> grouped = regionNames.stream()
                .collect(Collectors.groupingBy(
                        rc -> rc.split(" ")[0], // 상위 지역
                        LinkedHashMap::new,
                        Collectors.mapping(rc -> {
                            String[] parts = rc.split(" ");
                            if (parts.length == 1) {
                                return "전체";
                            } else {
                                return parts[1];
                            }
                        }, Collectors.toList())
                ));

        return grouped.entrySet().stream()
                .map(entry -> {
                    if (entry.getValue().size() == 1 && "전체".equals(entry.getValue().get(0))) {
                        return entry.getKey() + " 전체";
                    }
                    return entry.getKey() + " " + String.join(", ", entry.getValue());
                })
                .collect(Collectors.joining(", "));
    }

}

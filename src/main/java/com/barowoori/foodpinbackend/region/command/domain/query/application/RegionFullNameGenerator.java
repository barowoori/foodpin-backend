package com.barowoori.foodpinbackend.region.command.domain.query.application;

import com.barowoori.foodpinbackend.region.command.domain.model.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

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

}

package com.barowoori.foodpinbackend.region.command.domain.query.application;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.region.command.domain.exception.RegionErrorCode;
import com.barowoori.foodpinbackend.region.command.domain.model.*;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
import com.querydsl.core.Tuple;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionDo.regionDo;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionGu.regionGu;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionGun.regionGun;
import static com.barowoori.foodpinbackend.region.command.domain.model.QRegionSi.regionSi;

@Getter
@AllArgsConstructor
public class RegionSearchProcessor {
    private List<RegionDo> regionDos;
    private List<RegionSi> regionSis;
    private List<RegionGu> regionGus;
    private List<RegionGun> regionGuns;

    private Set<String> regionDoIds;
    private Set<String> regionSiIds;
    private Set<String> regionGuIds;
    private Set<String> regionGunIds;

    public RegionSearchProcessor(List<RegionDo> regionDos,
                                 List<RegionSi> regionSis,
                                 List<RegionGu> regionGus,
                                 List<RegionGun> regionGuns) {
        this.regionDos = regionDos;
        this.regionSis = regionSis;
        this.regionGus = regionGus;
        this.regionGuns = regionGuns;

        this.regionDoIds = regionDos.stream().map(RegionDo::getId).collect(Collectors.toSet());
        this.regionSiIds = regionSis.stream().map(RegionSi::getId).collect(Collectors.toSet());
        this.regionGuIds = regionGus.stream().map(RegionGu::getId).collect(Collectors.toSet());
        this.regionGunIds = regionGuns.stream().map(RegionGun::getId).collect(Collectors.toSet());
    }

    public RegionInfo findByCode(String regionCode) {
        Map<String, String> codes = new HashMap<>();

        Pattern pattern = Pattern.compile("(SI|DO|GU|GUN)([a-fA-F0-9\\-]+)");
        Matcher matcher = pattern.matcher(regionCode);

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

    public String findFullRegionName(RegionType regionType, String regionId) {
        switch (regionType) {
            case REGION_DO:
                return regionDos.stream()
                        .filter(regionDo -> regionDo.getId().equals(regionId))
                        .map(RegionDo::getName)
                        .findFirst()
                        .orElse(null);

            case REGION_SI:
                return regionSis.stream()
                        .filter(regionSi -> regionSi.getId().equals(regionId))
                        .map(regionSi -> makeFullName(regionSi.getRegionDo(), regionSi))
                        .findFirst()
                        .orElse(null);

            case REGION_GU:
                return regionGus.stream()
                        .filter(regionGu -> regionGu.getId().equals(regionId))
                        .map(regionGu ->
                        {
                            RegionSi regionSi = regionGu.getRegionSi();
                            if (regionSi == null) {
                                return makeFullName(regionGu);
                            }
                            return makeFullName(regionSi.getRegionDo(), regionSi, regionGu);

                        })
                        .findFirst()
                        .orElse(null);

            case REGION_GUN:
                return regionGuns.stream()
                        .filter(regionGun -> regionGun.getId().equals(regionId))
                        .map(regionGun -> {
                            RegionSi regionSi = regionGun.getRegionSi();
                            if (regionSi == null) {
                                return makeFullName(regionGun);
                            }
                            return makeFullName(regionSi.getRegionDo(), regionSi, regionGun);
                        })
                        .findFirst()
                        .orElse(null);

            default:
                return null;
        }
    }

    private boolean isExistDO(String id) {
        return regionDoIds.contains(id);
    }

    private boolean isExistSI(String id) {
        return regionSiIds.contains(id);
    }

    private boolean isExistGU(String id) {
        return regionGuIds.contains(id);
    }

    private boolean isExistGUN(String id) {
        return regionGunIds.contains(id);
    }

    private RegionDo findDoById(String id) {
        return regionDos.stream()
                .filter(doObj -> doObj.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private RegionSi findSiById(String id) {
        return regionSis.stream()
                .filter(si -> si.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private String makeFullName(Region... regions) {
        return Arrays.stream(regions)
                .filter(Objects::nonNull)
                .map(Region::getName)
                .collect(Collectors.joining(" "));
    }
}

package com.barowoori.foodpinbackend.region.command.domain.repository.dto;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionInfo {
    private RegionType regionType;
    private String regionId;

    public static RegionInfo of(RegionType regionType, String regionId){
        return RegionInfo.builder()
                .regionType(regionType)
                .regionId(regionId)
                .build();
    }
}

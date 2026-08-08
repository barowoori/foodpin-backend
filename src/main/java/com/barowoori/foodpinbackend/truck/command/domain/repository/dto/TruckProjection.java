package com.barowoori.foodpinbackend.truck.command.domain.repository.dto;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import lombok.Getter;

import java.util.List;

@Getter
public class TruckProjection {
    private final String id;
    private final String name;
    private final Integer avgMenuPrice;
    private final String mainPhotoPath;
    private final List<String> menuNames;
    private final List<String> menuPhotoPaths;
    private final List<RegionEntry> regions;

    public TruckProjection(String id, String name, Integer avgMenuPrice, String mainPhotoPath,
                           List<String> menuNames, List<String> menuPhotoPaths, List<RegionEntry> regions) {
        this.id = id;
        this.name = name;
        this.avgMenuPrice = avgMenuPrice;
        this.mainPhotoPath = mainPhotoPath;
        this.menuNames = menuNames != null ? menuNames : List.of();
        this.menuPhotoPaths = menuPhotoPaths != null ? menuPhotoPaths : List.of();
        this.regions = regions != null ? regions : List.of();
    }

    public record RegionEntry(RegionType regionType, String regionId) {}
}

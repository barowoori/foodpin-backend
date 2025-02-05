package com.barowoori.foodpinbackend.truck.command.application.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestTruck {
    @Builder
    @Data
    public static class CreateTruckDto{
        @NotEmpty
        private TruckInfoDto truckInfoDto;
        @NotEmpty
        private TruckRegionDto truckRegionDto;
        @NotEmpty
        private Set<TruckCategoryDto> truckCategoryDtoSet;
        private List<TruckMenuDto> truckMenuDtoList;
        private Set<TruckDocumentDto> truckDocumentDtoSet;
    }

    @Getter
    public static class TruckInfoDto{
        @NotEmpty
        private String name;
        private String description;
        private Boolean electricityUsage;
        private Boolean gasUsage;
        private Boolean selfGenerationAvailability;
        private List<String> fileIdList;

        public Truck toEntity(){
            return Truck.builder()
                    .name(this.name)
                    .description(this.description)
                    .electricityUsage(this.electricityUsage)
                    .gasUsage(this.gasUsage)
                    .selfGenerationAvailability(this.selfGenerationAvailability)
                    .build();
        }
    }

    @Getter
    public static class TruckRegionDto{
        @NotEmpty
        private RegionType regionType;
        @NotEmpty
        private String regionId;

        public TruckRegion toEntity(Truck truck){
            return TruckRegion.builder()
                    .regionType(this.regionType)
                    .regionId(this.regionId)
                    .truck(truck)
                    .build();
        }
    }

    @Getter
    public static class TruckCategoryDto{
        private String categoryId;

        public TruckCategory toEntity(Truck truck, Category category){
            return TruckCategory.builder()
                    .truck(truck)
                    .category(category)
                    .build();
        }
    }

    @Getter
    public static class TruckMenuDto{
        @NotEmpty
        private String name;
        private String description;
        private Integer price;
        private List<String> fileIdList;

        public TruckMenu toEntity(Truck truck){
            return TruckMenu.builder()
                    .name(this.name)
                    .description(this.description)
                    .price(this.price)
                    .truck(truck)
                    .build();
        }
    }

    @Getter
    public static class TruckDocumentDto{
        @NotEmpty
        private DocumentType type;
        @NotEmpty
        private Boolean approval;
        @NotEmpty
        private List<String> fileIdList;

        public TruckDocument toEntity(String updatedBy, String path, Truck truck){
            return TruckDocument.builder()
                    .updatedBy(updatedBy)
                    .type(this.type)
                    .path(path)
                    .approval(this.approval)
                    .truck(truck)
                    .build();
        }
    }
}

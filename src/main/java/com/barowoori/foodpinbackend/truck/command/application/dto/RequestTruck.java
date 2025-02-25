package com.barowoori.foodpinbackend.truck.command.application.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

public class RequestTruck {
    @Builder
    @Data
    public static class CreateTruckDto{
        @NotEmpty
        private TruckInfoDto truckInfoDto;
        @NotEmpty
        private Set<TruckRegionDto> truckRegionDtoSet;
        @NotEmpty
        private Set<TruckCategoryDto> truckCategoryDtoSet;
        @NotEmpty
        private List<TruckMenuDto> truckMenuDtoList;
        private Set<TruckDocumentDto> truckDocumentDtoSet;
    }

    @Getter
    public static class TruckInfoDto{
        @Schema(description = "트럭 이름")
        @NotEmpty
        private String name;
        @Schema(description = "트럭 설명")
        private String description;
        @Schema(description = "전기 사용 여부")
        @NotEmpty
        private Boolean electricityUsage;
        @Schema(description = "가스 사용 여부")
        @NotEmpty
        private Boolean gasUsage;
        @Schema(description = "자가 발전 가능 여부")
        @NotEmpty
        private Boolean selfGenerationAvailability;
        @Schema(description = "트럭 사진 파일 id 리스트")
        @NotEmpty
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
        @Schema(description = "지역 타입")
        @NotEmpty
        private RegionType regionType;
        @Schema(description = "지역 id")
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
        @Schema(description = "트럭 카테고리 id")
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
        @Schema(description = "메뉴 이름")
        @NotEmpty
        private String name;
        @Schema(description = "메뉴 설명")
        private String description;
        @Schema(description = "메뉴 가격")
        private Integer price;
        @Schema(description = "메뉴 사진 파일 id 리스트")
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
        @Schema(description = "트럭 서류 타입")
        @NotEmpty
        private DocumentType type;
        @Schema(description = "트럭 서류 검증 여부")
        @NotEmpty
        private Boolean approval;
        @Schema(description = "트럭 서류 사진 파일 id 리스트")
        @NotEmpty
        private List<String> fileIdList;

        public TruckDocument toEntity(String updatedBy, String path, Truck truck){
            return TruckDocument.builder()
                    .updatedBy(updatedBy)
                    .type(this.type)
                    .documentId("documentId")
                    .approval(this.approval)
                    .truck(truck)
                    .build();
        }
    }

    @Getter
    public static class UpdateTruckInfoDto{
        @Schema(description = "트럭 이름")
        @NotEmpty
        private String name;
        @Schema(description = "트럭 설명")
        private String description;
        @Schema(description = "트럭 사진 파일 id 리스트")
        @NotEmpty
        private List<String> fileIdList;
    }

    @Getter
    public static class UpdateTruckOperationDto{
        @Schema(description = "전기 사용 여부")
        @NotEmpty
        private Boolean electricityUsage;
        @Schema(description = "가스 사용 여부")
        @NotEmpty
        private Boolean gasUsage;
        @Schema(description = "자가 발전 가능 여부")
        @NotEmpty
        private Boolean selfGenerationAvailability;
        @NotEmpty
        private Set<TruckRegionDto> truckRegionDtoSet;
    }

    @Getter
    public static class UpdateTruckMenuDto{
        @NotEmpty
        private Set<TruckCategoryDto> truckCategoryDtoSet;
        @NotEmpty
        private List<TruckMenuDto> truckMenuDtoList;
    }
}

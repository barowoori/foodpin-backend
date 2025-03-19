package com.barowoori.foodpinbackend.truck.command.application.dto;

import com.barowoori.foodpinbackend.document.command.application.dto.RequestDocument;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
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
        @Schema(description = "트럭 기본 정보")
        @NotEmpty
        private TruckInfoDto truckInfoDto;
        @Schema(description = "트럭 지역 코드 Set")
        @NotEmpty
        private Set<String> truckRegionCodeSet;
        @Schema(description = "트럭 카테고리 코드 Set")
        @NotEmpty
        private Set<String> truckCategoryCodeSet;
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
        @NotEmpty
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

        public Truck toEntity(String creator){
            return Truck.builder()
                    .name(this.name)
                    .updatedBy(creator)
                    .description(this.description)
                    .electricityUsage(this.electricityUsage)
                    .gasUsage(this.gasUsage)
                    .selfGenerationAvailability(this.selfGenerationAvailability)
                    .views(0)
                    .isDeleted(Boolean.FALSE)
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
        @NotEmpty
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
        @Schema(description = "사업자 정보 dto, 사업자 등록증인 경우에만 보내주면 됩니다.")
        private RequestDocument.CreateBusinessRegistrationDto createBusinessRegistrationDto;
        @Schema(description = "트럭 서류 검증 여부")
        @NotEmpty
        private Boolean approval;
        @Schema(description = "트럭 서류 사진 파일 id 리스트")
        private List<String> fileIdList;

        public TruckDocument toEntity(String updatedBy, String documentId, Truck truck){
            return TruckDocument.builder()
                    .updatedBy(updatedBy)
                    .type(this.type)
                    .documentId(documentId)
                    .approval(this.approval)
                    .truck(truck)
                    .build();
        }

        public TruckDocument toEntity(String updatedBy, Truck truck) {
            return TruckDocument.builder()
                    .updatedBy(updatedBy)
                    .type(this.type)
                    .approval(this.approval)
                    .truck(truck)
                    .build();
        }
    }

    @Getter
    @Data
    public static class UpdateTruckInfoDto{
        @Schema(description = "트럭 이름")
        @NotEmpty
        private String name;
        @Schema(description = "트럭 설명")
        @NotEmpty
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
        @Schema(description = "트럭 지역 코드 Set")
        @NotEmpty
        private Set<String> truckRegionCodeSet;
    }

    @Getter
    public static class UpdateTruckMenuDto{
        @Schema(description = "트럭 카테고리 코드 Set")
        @NotEmpty
        private Set<String> truckCategoryCodeSet;
        @NotEmpty
        private List<TruckMenuDto> truckMenuDtoList;
    }
}


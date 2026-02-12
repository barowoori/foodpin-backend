package com.barowoori.foodpinbackend.truck.command.application.dto;

import com.barowoori.foodpinbackend.document.command.application.dto.RequestDocument;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.barowoori.foodpinbackend.common.validation.UnicodeSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Set;

public class RequestTruck {
    @Builder
    @Data
    public static class CreateTruckDto{
        @Valid
        @Schema(description = "트럭 기본 정보")
        @NotNull
        private TruckInfoDto truckInfoDto;

        @Schema(description = "트럭 지역 코드 Set")
        @NotEmpty
        private List<String> truckRegionCodeSet;

        @Schema(description = "트럭 카테고리 코드 Set")
        @NotEmpty
        private Set<String> truckCategoryCodeSet;

        @Valid
        @NotEmpty
        private List<TruckMenuDto> truckMenuDtoList;

        @Valid
        private Set<TruckDocumentDto> truckDocumentDtoSet;
    }

    @Getter
    public static class TruckInfoDto{
        @UnicodeSize(min = 1, max = 30, message = "1자 이상 30자 이하로 입력하세요.")
        @Schema(description = "트럭 이름")
        @NotEmpty
        private String name;

        @UnicodeSize(min = 10, max = 10000, message = "10자 이상 10,000자 이하로 입력하세요.")
        @Schema(description = "트럭 설명")
        @NotEmpty
        private String description;

        @Schema(description = "전기 사용 여부")
        @NotNull
        private Boolean electricityUsage;

        @Schema(description = "가스 사용 여부")
        @NotNull
        private Boolean gasUsage;

        @Schema(description = "자가 발전 가능 여부")
        @NotNull
        private Boolean selfGenerationAvailability;

        @Schema(description = "트럭 사진 파일 id 리스트")
        @NotEmpty
        private List<String> fileIdList;

        @Schema(description = "트럭 색상")
        @NotEmpty
        private Set<TruckColor> truckColors;

        @Schema(description = "차량 형태")
        @NotNull
        private TruckBodyType bodyType;

        @Schema(description = "케이터링 가능 여부")
        @NotNull
        private Boolean isCatering;

        @Schema(description = "트럭 유형")
        @NotEmpty
        private Set<TruckType> types;

        @Schema(description = "결제 방식")
        @NotEmpty
        private Set<PaymentMethod> paymentMethods;

        @Schema(description = "증빙 발급 유형")
        @NotEmpty
        private Set<ProofIssuanceType> proofIssuanceTypes;

        public Truck toEntity(String creator){
            return Truck.builder()
                    .name(this.name)
                    .updatedBy(creator)
                    .description(this.description)
                    .electricityUsage(this.electricityUsage)
                    .gasUsage(this.gasUsage)
                    .selfGenerationAvailability(this.selfGenerationAvailability)
                    .colors(this.truckColors)
                    .bodyType(this.bodyType)
                    .isCatering(this.isCatering)
                    .types(this.types)
                    .paymentMethods(this.paymentMethods)
                    .proofIssuanceTypes(this.proofIssuanceTypes)
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
        @NotNull
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
        @NotNull
        private DocumentType type;

        @Schema(description = "사업자 정보 dto, 사업자 등록증인 경우 필수")
        @Valid
        private RequestDocument.CreateBusinessRegistrationDto createBusinessRegistrationDto;

        @Schema(description = "트럭 서류 사진 파일 id 리스트")
        private List<String> fileIdList;

        public TruckDocument toEntity(String updatedBy, String documentId, Truck truck){
            return TruckDocument.builder()
                    .updatedBy(updatedBy)
                    .type(this.type)
                    .documentId(documentId)
                    .truck(truck)
                    .build();
        }

        public TruckDocument toEntity(String updatedBy, Truck truck) {
            return TruckDocument.builder()
                    .updatedBy(updatedBy)
                    .type(this.type)
                    .truck(truck)
                    .build();
        }
    }

    @Getter
    @Data
    public static class UpdateTruckInfoDto{
        @UnicodeSize(min = 1, max = 30, message = "1자 이상 30자 이하로 입력하세요.")
        @Schema(description = "트럭 이름")
        @NotEmpty
        private String name;

        @UnicodeSize(min = 10, max = 10000, message = "10자 이상 10,000자 이하로 입력하세요.")
        @Schema(description = "트럭 설명")
        @NotEmpty
        private String description;

        @Schema(description = "트럭 사진 파일 id 리스트")
        @NotEmpty
        private List<String> fileIdList;

        @Schema(description = "트럭 색상")
        @NotEmpty
        private Set<TruckColor> truckColors;

        @Schema(description = "차량 형태")
        @NotNull
        private TruckBodyType bodyType;
    }

    @Getter
    public static class UpdateTruckOperationDto{
        @Schema(description = "전기 사용 여부")
        @NotNull
        private Boolean electricityUsage;

        @Schema(description = "가스 사용 여부")
        @NotNull
        private Boolean gasUsage;

        @Schema(description = "자가 발전 가능 여부")
        @NotNull
        private Boolean selfGenerationAvailability;

        @Schema(description = "트럭 지역 코드 Set")
        @NotEmpty
        private Set<String> truckRegionCodeSet;

        @Schema(description = "케이터링 가능 여부")
        @NotNull
        private Boolean isCatering;
    }

    @Getter
    public static class UpdateTruckMenuDto{
        @Schema(description = "트럭 카테고리 코드 Set")
        @NotEmpty
        private Set<String> truckCategoryCodeSet;

        @Schema(description = "트럭 유형")
        @NotEmpty
        private Set<TruckType> types;

        @Valid
        @NotEmpty
        private List<TruckMenuDto> truckMenuDtoList;
    }

    @Getter
    public static class UpdateTruckPaymentDto{
        @Schema(description = "결제 방식")
        @NotEmpty
        private Set<PaymentMethod> paymentMethods;

        @Schema(description = "증빙 발급 유형")
        @NotEmpty
        private Set<ProofIssuanceType> proofIssuanceTypes;
    }

    @Getter
    public static class AddManagerDto{
        @Schema(description = "운영자 추가할 트럭 id")
        @NotEmpty
        private String truckId;

        @Schema(description = "초대 코드")
        @NotEmpty
        private String code;
    }

    @Getter
    @Data
    public static class GetTruckManagerIdDto{
        @Schema(description = "트럭 관리자 id")
        @NotEmpty
        private String truckManagerId;
    }
}

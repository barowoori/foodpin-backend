package com.barowoori.foodpinbackend.truck.command.application.dto;

import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

public class ResponseTruck {
    @Builder
    @Data
    public static class GetTruckInviteMessageDto {
        @Schema(description = "트럭 운영자 초대문구")
        private String message;
    }

    @Builder
    @Data
    public static class GetTruckNameDto {
        private String id;
        private String name;

        public static GetTruckNameDto of(Truck truck) {
            return GetTruckNameDto.builder()
                    .id(truck.getId())
                    .name(truck.getName())
                    .build();
        }
    }

    @Builder
    @Data
    public static class GetBusinessRegistrationInfo {
        private String businessNumber;
        private String businessName;
        private String representativeName;
        private LocalDate openingDate;

        public static GetBusinessRegistrationInfo of(BusinessRegistration businessRegistration) {
            GetBusinessRegistrationInfo.GetBusinessRegistrationInfoBuilder builder = GetBusinessRegistrationInfo.builder();
            if (businessRegistration != null) {
                builder.businessNumber(businessRegistration.getBusinessNumber())
                        .businessName(businessRegistration.getBusinessName())
                        .representativeName(businessRegistration.getRepresentativeName())
                        .openingDate(businessRegistration.getOpeningDate());
            }
            return builder.build();
        }
    }
}

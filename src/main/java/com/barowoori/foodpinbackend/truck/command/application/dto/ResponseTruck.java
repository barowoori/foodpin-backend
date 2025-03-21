package com.barowoori.foodpinbackend.truck.command.application.dto;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

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

        public static GetTruckNameDto of(Truck truck){
            return GetTruckNameDto.builder()
                    .id(truck.getId())
                    .name(truck.getName())
                    .build();
        }
    }
}

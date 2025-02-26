package com.barowoori.foodpinbackend.document.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

public class ResponseDocument {
    @Builder
    @Data
    public static class validDto{
        @Schema(description = "진위 여부")
        private Boolean isvalid;

        public static validDto toDto(Boolean isValid){
            return validDto.builder()
                    .isvalid(isValid)
                    .build();
        }
    }
}

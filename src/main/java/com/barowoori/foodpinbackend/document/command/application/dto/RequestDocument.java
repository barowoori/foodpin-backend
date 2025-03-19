package com.barowoori.foodpinbackend.document.command.application.dto;

import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

public class RequestDocument {
    @Data
    @Builder
    @Getter
    public static class CreateBusinessRegistrationDto{
        @Schema(description = "사업자 등록 번호")
        @NotEmpty
        private String businessNumber;
        @Schema(description = "상호명")
        @NotEmpty
        private String businessName;
        @Schema(description = "대표자명")
        @NotEmpty
        private String representativeName;
        @Schema(description = "개업일자")
        @NotEmpty
        private LocalDate openingDate;

        public BusinessRegistration toEntity(String updatedBy){
            return BusinessRegistration.builder()
                    .updatedBy(updatedBy)
                    .businessNumber(this.businessNumber)
                    .businessName(this.businessName)
                    .representativeName(this.representativeName)
                    .openingDate(this.openingDate)
                    .build();
        }
    }
}

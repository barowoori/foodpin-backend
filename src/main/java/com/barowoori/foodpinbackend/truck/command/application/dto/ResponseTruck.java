package com.barowoori.foodpinbackend.truck.command.application.dto;

import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocumentPhoto;
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

    @Builder
    @Data
    public static class GetTruckDocumentFile {
        private DocumentType type;
        private String fileId;
        private String path;
        private String fileName;

        public static GetTruckDocumentFile of(TruckDocument truckDocument, ImageManager imageManager) {
            TruckDocumentPhoto truckDocumentPhoto = truckDocument.getPhotos().stream().findFirst().orElse(null);
            if (truckDocumentPhoto == null){
                return GetTruckDocumentFile.builder()
                        .type(truckDocument.getType())
                        .build();
            }
            File file = truckDocumentPhoto.getFile();
            return GetTruckDocumentFile.builder()
                    .type(truckDocument.getType())
                    .fileId(file.getId())
                    .path(file.getPreSignUrl(imageManager))
                    .fileName(file.getFileName())
                    .build();
        }
    }
}

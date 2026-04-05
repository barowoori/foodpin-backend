package com.barowoori.foodpinbackend.truck.command.application.dto;

import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocumentStatus;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocumentPhoto;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.BackOfficeTruckDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    public static class GetAppliedEventCountDto {
        private Integer count;

        public static GetAppliedEventCountDto of(Integer count) {
            return GetAppliedEventCountDto.builder()
                    .count(count)
                    .build();
        }
    }

    @Builder
    @Data
    public static class GetMaxAvgMenuPriceDto {
        private Integer maxAvgMenuPrice;

        public static GetMaxAvgMenuPriceDto of(Integer maxAvgMenuPrice) {
            return GetMaxAvgMenuPriceDto.builder()
                    .maxAvgMenuPrice(maxAvgMenuPrice)
                    .build();
        }
    }

    @Builder
    @Data
    public static class GetTruckUpdateAvailabilityDto {
        private Boolean isAvailableUpdate;

        public static GetTruckUpdateAvailabilityDto of(Boolean isAvailableUpdate) {
            return GetTruckUpdateAvailabilityDto.builder()
                    .isAvailableUpdate(isAvailableUpdate)
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
            if (truckDocumentPhoto == null) {
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

    @Builder
    @Data
    public static class GetTruckManagerContactDto {
        private String phone;

        public static GetTruckManagerContactDto of(String phone) {
            return GetTruckManagerContactDto.builder()
                    .phone(phone)
                    .build();
        }
    }

    @Builder
    @Data
    public static class GetBackOfficeTruckDocumentDto {
        private String truckId;
        private DocumentType documentType;
        private String nickname;
        private String phoneNumber;

        private String businessRegistrationNumber;
        private String representativeName;
        private String businessName;
        private LocalDate openingDate;
        private List<String> imageUrls;
        private TruckDocumentStatus status;
        private String rejectionReason;

        private LocalDateTime requestedAt;
        private LocalDateTime processedAt;
        private String documentId;

        public static GetBackOfficeTruckDocumentDto of(BackOfficeTruckDocument dto, List<String> imageUrls) {
            TruckDocument truckDocument = dto.getTruckDocument();
            Member member = dto.getMember();
            BusinessRegistration businessRegistration = dto.getBusinessRegistration();
            return GetBackOfficeTruckDocumentDto.builder()
                    .truckId(truckDocument.getTruck().getId())
                    .documentType(truckDocument.getType())
                    .nickname(member != null ? member.getNickname() : null)
                    .phoneNumber(member != null ? member.getPhone() : null)
                    .businessRegistrationNumber(businessRegistration.getBusinessNumber())
                    .representativeName(businessRegistration.getRepresentativeName())
                    .businessName(businessRegistration.getBusinessName())
                    .openingDate(businessRegistration.getOpeningDate())
                    .imageUrls(imageUrls)
                    .status(truckDocument.getStatus())
                    .requestedAt(truckDocument.getCreatedAt())
                    .processedAt(truckDocument.getProcessedAt())
                    .rejectionReason(truckDocument.getRejectionReason())
                    .status(truckDocument.getStatus())
                    .documentId(truckDocument.getDocumentId())
                    .build();
        }
    }

}

package com.barowoori.foodpinbackend.truck.command.domain.repository.dto;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocumentStatus;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckPhoto;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class TruckList {
    private String id;
    private String name;
    private List<DocumentType> documents;
    private Boolean businessRegistrationApproved;
    private List<String> regions;
    private String regionList;
    private List<String> menuNames;
    private String photo;
    private Integer avgMenuPrice;
    private List<String> menuPhotos;

    public static TruckList of(Truck truck, List<TruckDocumentInfoDto> documents, List<String> regions, String regionList, ImageManager imageManager) {
        return TruckList.builder()
                .id(truck.getId())
                .name(truck.getName())
                .businessRegistrationApproved(documents != null ?
                        documents.stream()
                                .filter(document -> document.getType().equals(DocumentType.BUSINESS_REGISTRATION))
                                .map(TruckDocumentInfoDto::getStatus)
                                .anyMatch(status -> status.equals(TruckDocumentStatus.APPROVED)) : Boolean.FALSE)
                .documents(documents != null
                        ? documents.stream()
                        .filter(document -> document.getType() != DocumentType.BUSINESS_REGISTRATION
                                || document.getStatus() == TruckDocumentStatus.APPROVED)
                        .map(TruckDocumentInfoDto::getType)
                        .distinct()
                        .toList()
                        : new ArrayList<>())
                .regions(regions)
                .regionList(regionList)
                .menuNames(truck.getSortedTruckMenuNames())
                .photo(truck.getTruckMainPhotoUrl(imageManager))
                .avgMenuPrice(truck.getAvgMenuPrice())
                .menuPhotos(truck.getFirstTwoCreatedTruckMenuPhotos(imageManager))
                .build();
    }
}

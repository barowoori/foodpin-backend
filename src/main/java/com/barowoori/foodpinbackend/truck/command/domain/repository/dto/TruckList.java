package com.barowoori.foodpinbackend.truck.command.domain.repository.dto;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckPhoto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TruckList {
        private String id;
        private String name;
        private List<DocumentType> documents;
        private List<String> regions;
        private List<String> menuNames;
        private String photo;

        public static TruckList of(Truck truck, List<DocumentType> documents, List<String> regions, ImageManager imageManager) {

            return TruckList.builder()
                    .id(truck.getId())
                    .name(truck.getName())
                    .documents(documents)
                    .regions(regions)
                    .menuNames(truck.getSortedTruckMenuNames())
                    .photo(truck.getTruckMainPhotoUrl(imageManager))
                    .build();
        }
}

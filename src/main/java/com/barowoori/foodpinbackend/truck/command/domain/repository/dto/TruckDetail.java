package com.barowoori.foodpinbackend.truck.command.domain.repository.dto;

import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TruckDetail {
    private Boolean isAvailableUpdate;
    private Boolean isAvailableDelete;
    private TruckInfo truck;
    private List<DocumentType> documents;
    private List<String> regions;
    private List<MenuInfo> menus;
    private Boolean isLike;

    public static TruckDetail of(TruckManager truckManager, Truck truck, List<DocumentType> documents, List<String> regions, List<TruckMenu> truckMenus, Boolean isLike) {
        return TruckDetail.builder()
                .isAvailableUpdate(checkAvailableUpdate(truckManager))
                .isAvailableDelete(checkAvailableDelete(truckManager))
                .truck(TruckInfo.of(truck))
                .documents(documents)
                .regions(regions)
                .menus(truckMenus.stream().map(MenuInfo::of).toList())
                .isLike(isLike)
                .build();
    }

    private static Boolean checkAvailableUpdate(TruckManager truckManager) {
        if (truckManager == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private static Boolean checkAvailableDelete(TruckManager truckManager) {
        if (truckManager == null) {
            return Boolean.FALSE;
        }
        return truckManager.getRole().equals(TruckManagerRole.OWNER);
    }

    @Getter
    @Builder
    public static class TruckInfo {
        private String id;
        private String name;
        private String description;
        private Boolean electricityUsage;
        private Boolean gasUsage;
        private Boolean selfGenerationAvailability;
        private List<Photo> photos;

        public static TruckInfo of(Truck truck) {
            return TruckInfo.builder()
                    .id(truck.getId())
                    .name(truck.getName())
                    .description(truck.getDescription())
                    .electricityUsage(truck.getElectricityUsage())
                    .gasUsage(truck.getGasUsage())
                    .selfGenerationAvailability(truck.getSelfGenerationAvailability())
                    .photos(truck.getPhotos()
                            .stream()
                            .map(Photo::ofTruckPhoto)
                            .toList())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MenuInfo {
        private String id;
        private String name;
        private Integer price;
        private String description;
        private List<Photo> photos;

        public static MenuInfo of(TruckMenu truckMenu) {
            return MenuInfo.builder()
                    .id(truckMenu.getId())
                    .name(truckMenu.getName())
                    .price(truckMenu.getPrice())
                    .description(truckMenu.getDescription())
                    .photos(truckMenu.getPhotos()
                            .stream()
                            .map(Photo::ofTruckMenuPhoto)
                            .toList())

                    .build();

        }

    }

    @Getter
    @Builder
    public static class Photo {
        private String id;
        private String path;

        public static Photo ofTruckPhoto(TruckPhoto truckPhoto) {
            return Photo.builder()
                    .id(truckPhoto.getId())
                    .path(truckPhoto.getPath())
                    .build();
        }

        public static Photo ofTruckMenuPhoto(TruckMenuPhoto truckMenuPhoto) {
            return Photo.builder()
                    .id(truckMenuPhoto.getId())
                    .path(truckMenuPhoto.getPath())
                    .build();
        }
    }

}

package com.barowoori.foodpinbackend.truck.command.domain.repository.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Getter
@SuperBuilder
public class TruckDetail {
    private Boolean isTruckManager;
    private Boolean isAvailableUpdate;
    private Boolean isAvailableDelete;
    private TruckInfo truck;
    private List<DocumentType> documents;
    private List<TruckDocumentInfo> documentInfos;
    private List<RegionCode> regions;
    private List<CategoryInfo> categories;
    private List<MenuInfo> menus;
    private Boolean isLike;

    public static TruckDetail of(TruckManager truckManager, Truck truck, TruckDocumentManager truckDocumentManager, List<RegionCode> regions, List<Category> categories,List<TruckMenu> truckMenus, Boolean isLike, ImageManager imageManager) {
        return TruckDetail.builder()
                .isTruckManager(truckManager != null)
                .isAvailableUpdate(checkAvailableUpdate(truckManager))
                .isAvailableDelete(checkAvailableDelete(truckManager))
                .truck(TruckInfo.of(truck, imageManager))
                .documents(truckDocumentManager.getTypes())
                .documentInfos(truckDocumentManager.getDocuments().stream().map(TruckDocumentInfo::of).toList())
                .regions(regions)
                .categories(categories.stream()
                        .sorted(Comparator.comparing(Category::getCode))
                        .map(CategoryInfo::of).toList())
                .menus(truckMenus.stream()
                        .map(truckMenu -> MenuInfo.of(truckMenu, imageManager))
                        .toList())
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

        public static TruckInfo of(Truck truck, ImageManager imageManager) {
            return TruckInfo.builder()
                    .id(truck.getId())
                    .name(truck.getName())
                    .description(truck.getDescription())
                    .electricityUsage(truck.getElectricityUsage())
                    .gasUsage(truck.getGasUsage())
                    .selfGenerationAvailability(truck.getSelfGenerationAvailability())
                    .photos(truck.getTruckPhotoFiles().stream()
                            .map(file -> Photo.of(file, imageManager))
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

        public static MenuInfo of(TruckMenu truckMenu, ImageManager imageManager) {
            return MenuInfo.builder()
                    .id(truckMenu.getId())
                    .name(truckMenu.getName())
                    .price(truckMenu.getPrice())
                    .description(truckMenu.getDescription())
                    .photos(truckMenu.getTruckMenuPhotoFiles().stream()
                            .map(file -> Photo.of(file, imageManager))
                            .toList())
                    .build();

        }

    }

    @Getter
    @Builder
    public static class CategoryInfo {
        private String code;
        private String name;

        public static CategoryInfo of(Category category) {
            return CategoryInfo.builder()
                    .code(category.getCode())
                    .name(category.getName())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Photo {
        private String id;
        private String path;
        public static Photo of(File file, ImageManager imageManager) {
            return Photo.builder()
                    .id(file.getId())
                    .path(file.getPreSignUrl(imageManager))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class TruckDocumentInfo{
        private DocumentType type;
        private LocalDate date;

        public static TruckDocumentInfo of(TruckDocument truckDocument){
            return TruckDocumentInfo.builder()
                    .type(truckDocument.getType())
                    .date(truckDocument.getUpdatedAt().toLocalDate())
                    .build();
        }
    }
}

package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
public class EventDetail {
    private Boolean isAvailableUpdate;
    private Boolean isAvailableDelete;
    private Boolean isLike;
    private String id;
    private List<Photo> photos;
    private RecruitInfo recruitInfo;
    private String name;
    private List<RegionCode> regions;
    private List<EventDateInfo> dates;
    private Integer entryFee;
    private Boolean electricitySupportAvailability;
    private Boolean generatorRequirement;
    private List<CategoryInfo> categories;
    private List<DocumentType> documents;
    private String description;
    private String guidelines;

    public static EventDetail of(Event event, String memberId, Boolean isLike, ImageManager imageManager, List<RegionCode> regions) {
        return EventDetail.builder()
                .isAvailableUpdate(event.isCreator(memberId))
                .isAvailableDelete(event.isCreator(memberId))
                .id(event.getId())
                .photos(event.getEventPhotoFiles().stream()
                        .map(file -> Photo.of(file, imageManager)).toList())
                .recruitInfo(RecruitInfo.of(event, event.getRecruitDetail()))
                .name(event.getName())
                .regions(regions)
                .dates(event.getSortedEventDates().stream()
                        .map(EventDateInfo::of).toList())
                .entryFee(event.getRecruitDetail().getEntryFee())
                .electricitySupportAvailability(event.getRecruitDetail().getElectricitySupportAvailability())
                .generatorRequirement(event.getRecruitDetail().getGeneratorRequirement())
                .categories(event.getCategories().stream().map(EventCategory::getCategory).map(CategoryInfo::of).toList())
                .documents(event.getDocuments().stream().map(EventDocument::getType).toList())
                .description(event.getDescription())
                .guidelines(event.getGuidelines())
                .isLike(isLike)
                .build();

    }


    @Getter
    @Builder
    public static class EventDateInfo {
        private String id;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;

        public static EventDateInfo of(EventDate eventDate) {
            return EventDateInfo.builder()
                    .id(eventDate.getId())
                    .date(eventDate.getDate())
                    .startTime(eventDate.getStartTime())
                    .endTime(eventDate.getEndTime())
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
    public static class RecruitInfo {
        private EventStatus status;
        private String statusComment;
        private Integer applicantCount;
        private Integer selectedCount;
        private Integer recruitCount;

        public static RecruitInfo of(Event event, EventRecruitDetail eventRecruitDetail) {
            return RecruitInfo.builder()
                    .status(event.getStatus())
                    .statusComment(event.getStatusComment())
                    .applicantCount(eventRecruitDetail.getApplicantCount())
                    .selectedCount(eventRecruitDetail.getSelectedCount())
                    .recruitCount(eventRecruitDetail.getRecruitCount())
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
}

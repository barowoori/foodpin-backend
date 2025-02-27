package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckList;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
public class EventList {
    private String id;
    private String photo;
    private String name;
    private LocalDate recruitEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> regions;
    private List<String> categories;
    private RecruitInfo recruitInfo;
    private Integer views;

    public static EventList of(Event event, List<String> regions, ImageManager imageManager) {
        return EventList.builder()
                .id(event.getId())
                .photo(event.getPhotos().stream()
                        .map(truckPhoto -> imageManager.getPreSignUrl(truckPhoto.getFile().getPath()))
                        .findFirst().orElse(null))
                .name(event.getName())
                .recruitEndDate(event.getRecruitDetail().getRecruitEndDate())
                .startDate(getMinDate(event.getEventDates()))
                .endDate(getMaxDate(event.getEventDates()))
                .regions(regions)
                .categories(event.getCategories().stream().map(EventCategory::getCategory).map(Category::getName).toList())
                .recruitInfo(RecruitInfo.of(event, event.getRecruitDetail()))
                .views(event.getView().getViews())
                .build();
    }

    private static LocalDate getMinDate(List<EventDate> eventDates) {
        return eventDates.stream()
                .map(EventDate::getDate)
                .min(Comparator.naturalOrder()).orElse(null);
    }

    private static LocalDate getMaxDate(List<EventDate> eventDates) {
        return eventDates.stream()
                .map(EventDate::getDate)
                .max(Comparator.naturalOrder()).orElse(null);
    }

    @Getter
    @Builder
    public static class RecruitInfo {
        private EventStatus status;
        private Integer applicantCount;
        private Integer selectedCount;
        private Integer recruitCount;

        public static RecruitInfo of(Event event, EventRecruitDetail eventRecruitDetail) {
            return RecruitInfo.builder()
                    .status(event.getStatus())
                    .applicantCount(eventRecruitDetail.getApplicantCount())
                    .selectedCount(eventRecruitDetail.getSelectedCount())
                    .recruitCount(eventRecruitDetail.getRecruitCount())
                    .build();
        }
    }
}

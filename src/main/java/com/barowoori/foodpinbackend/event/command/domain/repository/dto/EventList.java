package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.service.EventDateCalculator;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
public class EventList {
    private String id;
    private String photo;
    private String name;
    private LocalDateTime recruitEndDateTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private String region;
    private List<String> categories;
    private RecruitInfo recruitInfo;
    private Integer views;

    public static EventList of(Event event, List<String> regions, ImageManager imageManager) {
        return EventList.builder()
                .id(event.getId())
                .photo(event.getPhotos().stream()
                        .map(eventPhoto -> imageManager.getPreSignUrl(eventPhoto.getFile().getPath()))
                        .findFirst().orElse(null))
                .name(event.getName())
                .recruitEndDateTime(event.getRecruitDetail().getRecruitEndDateTime())
                .startDate(EventDateCalculator.getMinDate(event))
                .endDate(EventDateCalculator.getMaxDate(event))
                .region(regions.isEmpty() ? null : regions.getFirst())
                .categories(event.getCategories().stream().map(EventCategory::getCategory).map(Category::getName).toList())
                .recruitInfo(RecruitInfo.of(event, event.getRecruitDetail()))
                .views(event.getView().getViews())
                .build();
    }
}

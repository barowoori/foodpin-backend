package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.service.EventDateCalculator;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.model.EventCreatorType;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder
public class EventList {
    private String id;
    private EventCreatorType creatorType;
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
        LocalDate startDate = EventDateCalculator.getMinDate(event);
        LocalDate endDate = EventDateCalculator.getMaxDate(event);

        return EventList.builder()
                .id(event.getId())
                .creatorType(event.getCreatorType())
                .photo(event.getEventMainPhotoUrl(imageManager))
                .name(event.getName())
                .recruitEndDateTime(event.getRecruitDetail().getRecruitEndDateTime())
                .startDate(startDate)
                .endDate(endDate)
                .region(regions.isEmpty() ? null : regions.getFirst())
                .categories(event.getCategories().stream().map(EventCategory::getCategory).map(Category::getName).toList())
                .recruitInfo(RecruitInfo.of(event.getRecruitDetail(), endDate))
                .views(event.getView().getViews())
                .build();
    }
}

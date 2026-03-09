package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventCategory;
import com.barowoori.foodpinbackend.event.command.domain.service.EventDateCalculator;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class BackOfficeEventList extends EventList{
    private int recruitmentUrlClickCount;

    public static BackOfficeEventList of(Event event, List<String> regions, ImageManager imageManager) {
        return BackOfficeEventList.builder()
                .id(event.getId())
                .photo(event.getEventMainPhotoUrl(imageManager))
                .name(event.getName())
                .recruitEndDateTime(event.getRecruitDetail().getRecruitEndDateTime())
                .startDate(EventDateCalculator.getMinDate(event))
                .endDate(EventDateCalculator.getMaxDate(event))
                .recruitmentUrlClickCount(event.getRecruitmentUrlClickCount())
                .region(regions.isEmpty() ? null : regions.getFirst())
                .categories(event.getCategories().stream().map(EventCategory::getCategory).map(Category::getName).toList())
                .recruitInfo(RecruitInfo.of(event.getRecruitDetail()))
                .views(event.getView().getViews())
                .build();
    }
}

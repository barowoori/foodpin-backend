package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.service.EventDateCalculator;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class EventManageList {
    private String id;
    private String photo;
    private String name;
    private LocalDate recruitEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String region;
    private Integer views;
    private RecruitInfo recruitInfo;

    public static EventManageList of(Event event, List<String> regions, ImageManager imageManager) {
        return EventManageList.builder()
                .id(event.getId())
                .photo(event.getPhotos().stream()
                        .map(truckPhoto -> imageManager.getPreSignUrl(truckPhoto.getFile().getPath()))
                        .findFirst().orElse(null))
                .name(event.getName())
                .recruitEndDate(event.getRecruitDetail().getRecruitEndDate())
                .startDate(EventDateCalculator.getMinDate(event))
                .endDate(EventDateCalculator.getMaxDate(event))
                .region(regions.getFirst())
                .recruitInfo(RecruitInfo.of(event, event.getRecruitDetail()))
                .views(event.getView().getViews())
                .build();
    }

}

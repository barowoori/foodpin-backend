package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplicationDate;
import com.barowoori.foodpinbackend.event.command.domain.model.EventDate;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
public class TruckEventApplicationList {
    private String eventApplicationId;
    private String status;
    private List<LocalDate> dates;
    private EventInfo event;

    public static TruckEventApplicationList of(EventApplication eventApplication, String status, String region, ImageManager imageManager) {
        return TruckEventApplicationList.builder()
                .eventApplicationId(eventApplication.getId())
                .status(status)
                .dates(eventApplication.getDates().stream()
                        .map(EventApplicationDate::getEventDate)
                        .sorted(Comparator.comparing(EventDate::getDate))
                        .map(EventDate::getDate).toList())
                .event(EventInfo.of(eventApplication.getEvent(), region, imageManager))
                .build();
    }

    @Getter
    @Builder
    public static class EventInfo {
        private String id;
        private String photo;
        private String name;
        private String region;

        public static EventInfo of(Event event, String region, ImageManager imageManager) {
            return EventInfo.builder()
                    .id(event.getId())
                    .photo(event.getPhotos().stream()
                            .map(eventPhoto -> imageManager.getPreSignUrl(eventPhoto.getFile().getPath()))
                            .findFirst().orElse(null))
                    .name(event.getName())
                    .region(region)
                    .build();
        }
    }
}

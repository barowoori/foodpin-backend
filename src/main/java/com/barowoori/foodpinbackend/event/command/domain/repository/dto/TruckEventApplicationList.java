package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
public class TruckEventApplicationList {
    @Getter
    @Builder
    public static class AppliedInfo {
        private String eventApplicationId;
        private String status;
        private List<LocalDate> dates;
        private EventInfo event;

        public static AppliedInfo of(EventApplication eventApplication, String status, List<String> regions, ImageManager imageManager) {
            return AppliedInfo.builder()
                    .eventApplicationId(eventApplication.getId())
                    .status(status)
                    .dates(eventApplication.getDates().stream()
                            .map(EventApplicationDate::getEventDate)
                            .sorted(Comparator.comparing(EventDate::getDate))
                            .map(EventDate::getDate).toList())
                    .event(EventInfo.of(eventApplication.getEvent(), regions, imageManager))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SelectedInfo {
        private String eventTruckId;
        private String status;
        private List<LocalDate> dates;
        private EventInfo event;

        public static SelectedInfo of(EventTruck eventTruck, String status, List<String> regions, ImageManager imageManager) {
            return SelectedInfo.builder()
                    .eventTruckId(eventTruck.getId())
                    .status(status)
                    .dates(eventTruck.getDates().stream()
                            .map(EventTruckDate::getEventDate)
                            .sorted(Comparator.comparing(EventDate::getDate))
                            .map(EventDate::getDate).toList())
                    .event(EventInfo.of(eventTruck.getEvent(), regions, imageManager))
                    .build();
        }
    }


    @Getter
    @Builder
    public static class EventInfo {
        private String id;
        private String photo;
        private String name;
        private String region;

        public static EventInfo of(Event event, List<String> regions, ImageManager imageManager) {
            return EventInfo.builder()
                    .id(event.getId())
                    .photo(event.getPhotos().stream()
                            .map(eventPhoto -> imageManager.getPreSignUrl(eventPhoto.getFile().getPath()))
                            .findFirst().orElse(null))
                    .name(event.getName())
                    .region(regions.isEmpty() ? null : regions.getFirst())
                    .build();
        }
    }
}

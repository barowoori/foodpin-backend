package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class EventApplicationList {
    @Getter
    @Builder
    @Schema(name = "EventApplicationList.TruckInfo")
    public static class TruckInfo {
        private String id;
        private String name;
        private String photo;
        private List<String> menuNames;
        private Boolean approval;

        public static TruckInfo of(Truck truck, ImageManager imageManager) {
            return TruckInfo.builder()
                    .id(truck.getId())
                    .name(truck.getName())
                    .photo(truck.getTruckMainPhotoUrl(imageManager))
                    .approval(truck.approval())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class EventPendingApplication {
        private String eventApplicationId;
        private TruckInfo truck;
        private List<ApplicationDateInfo> dates;
        private Boolean isRead;


        public static EventPendingApplication of(EventApplication eventApplication, ImageManager imageManager) {
            return EventPendingApplication.builder()
                    .eventApplicationId(eventApplication.getId())
                    .truck(TruckInfo.of(eventApplication.getTruck(), imageManager))
                    .dates(eventApplication.getDates().stream()
                            .map(EventApplicationDate::getEventDate)
                            .sorted(Comparator.comparing(EventDate::getDate))
                            .map(ApplicationDateInfo::of).toList())
                    .isRead(eventApplication.getIsRead())
                    .build();

        }
    }

    @Getter
    @Builder
    public static class ApplicationDateInfo{
        private String eventDateId;
        private LocalDate date;

        public static ApplicationDateInfo of(EventDate eventDate){
            return ApplicationDateInfo.builder()
                    .eventDateId(eventDate.getId())
                    .date(eventDate.getDate())
                    .build();
        }
    }


    @Getter
    @Builder
    public static class EventSelectedApplication {
        private String eventTruckId;
        private TruckInfo truck;
        private List<LocalDate> dates;
        private EventTruckStatus status;

        public static EventSelectedApplication of(EventTruck eventTruck, ImageManager imageManager) {
            return EventSelectedApplication.builder()
                    .eventTruckId(eventTruck.getId())
                    .truck(TruckInfo.of(eventTruck.getTruck(), imageManager))
                    .dates(eventTruck.getDates().stream()
                            .map(EventTruckDate::getEventDate).map(EventDate::getDate).toList())
                    .status(eventTruck.getStatus())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class EventRejectedApplication {
        private String eventApplicationId;
        private TruckInfo truck;
        private List<LocalDate> dates;

        public static EventRejectedApplication of(EventApplication eventApplication, ImageManager imageManager) {
            return EventRejectedApplication.builder()
                    .eventApplicationId(eventApplication.getId())
                    .truck(TruckInfo.of(eventApplication.getTruck(), imageManager))
                    .dates(eventApplication.getDates().stream()
                            .map(EventApplicationDate::getEventDate).map(EventDate::getDate).toList())
                    .build();
        }
    }


}

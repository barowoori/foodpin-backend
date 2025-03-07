package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class EventApplicationList {
    @Getter
    @Builder
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
                    .photo(truck.getPhotos()
                            .stream()
                            .map(truckPhoto -> imageManager.getPreSignUrl(truckPhoto.getFile().getPath()))
                            .findFirst().orElse(null))
                    .approval(truck.approval())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class EventPendingApplication {
        private String id;
        private TruckInfo truck;
        //TODO 날짜인지 기간인지 확인
        private List<LocalDate> dates;
        private Boolean isRead;

        public static EventPendingApplication of(EventApplication eventApplication, ImageManager imageManager) {
            return EventPendingApplication.builder()
                    .id(eventApplication.getId())
                    .truck(TruckInfo.of(eventApplication.getTruck(), imageManager))
                    .dates(eventApplication.getDates().stream()
                            .map(EventApplicationDate::getEventDate).map(EventDate::getDate).toList())
                    .isRead(eventApplication.getIsRead())
                    .build();

        }
    }

    @Getter
    @Builder
    public static class EventSelectedApplication {
        private String id;
        private TruckInfo truck;
        //TODO 날짜인지 기간인지 확인
        private List<LocalDate> dates;
        private EventTruckStatus status;

        public static EventSelectedApplication of(EventTruck eventTruck, ImageManager imageManager) {
            return EventSelectedApplication.builder()
                    .id(eventTruck.getId())
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
        private String id;
        private TruckInfo truck;
        //TODO 날짜인지 기간인지 확인
        private List<LocalDate> dates;

        public static EventRejectedApplication of(EventApplication eventApplication, ImageManager imageManager) {
            return EventRejectedApplication.builder()
                    .id(eventApplication.getId())
                    .truck(TruckInfo.of(eventApplication.getTruck(), imageManager))
                    .dates(eventApplication.getDates().stream()
                            .map(EventApplicationDate::getEventDate).map(EventDate::getDate).toList())
                    .build();
        }
    }


}

package com.barowoori.foodpinbackend.event.command.application.dto;

import com.barowoori.foodpinbackend.event.command.domain.model.EventNotice;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ResponseEvent {
    @Builder
    @Data
    @Getter
    public static class GetEventNoticeDto{
        private String id;
        private String title;
        private LocalDateTime createdAt;

        public static GetEventNoticeDto of(EventNotice eventNotice){
            return GetEventNoticeDto.builder()
                    .id(eventNotice.getId())
                    .title(eventNotice.getTitle())
                    .createdAt(eventNotice.getCreatedAt())
                    .build();
        }
    }

    @Builder
    @Data
    @Getter
    public static class GetEventNoticeDetailForCreatorDto{
        private String id;
        private String title;
        private LocalDateTime createdAt;
        private String content;
        private Boolean isAvailableUpdate;
        private Boolean isAvailableDelete;
        private List<String> readTruckNames;
        private List<String> unreadTruckNames;

        public static GetEventNoticeDetailForCreatorDto of(EventNotice eventNotice){
            return GetEventNoticeDetailForCreatorDto.builder()
                    .id(eventNotice.getId())
                    .title(eventNotice.getTitle())
                    .content(eventNotice.getContent())
                    .createdAt(eventNotice.getCreatedAt())
                    .isAvailableUpdate(eventNotice.getReadEventTrucks().isEmpty())
                    .isAvailableDelete(eventNotice.getReadEventTrucks().isEmpty())
                    .readTruckNames(eventNotice.getReadEventTruckNames())
                    .unreadTruckNames(eventNotice.getUnReadEventTruckNames())
                    .build();
        }
    }

    @Builder
    @Data
    @Getter
    public static class GetEventNoticeDetailForTruckDto{
        private String id;
        private String title;
        private LocalDateTime createdAt;
        private String content;

        public static GetEventNoticeDetailForTruckDto of(EventNotice eventNotice){
            return GetEventNoticeDetailForTruckDto.builder()
                    .id(eventNotice.getId())
                    .title(eventNotice.getTitle())
                    .content(eventNotice.getContent())
                    .createdAt(eventNotice.getCreatedAt())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetTruckAppliedEventDashboard {
        private Integer appliedCount;
        private Integer progressCount;
        private Integer endCount;

        public static GetTruckAppliedEventDashboard of(Integer appliedCount, Integer progressCount, Integer endCount) {
            return GetTruckAppliedEventDashboard.builder()
                    .appliedCount(appliedCount)
                    .progressCount(progressCount)
                    .endCount(endCount)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetEventDashboard {
        private Integer recruitingCount;
        private Integer progressCount;
        private Integer endCount;

        public static GetEventDashboard of(Integer recruitingCount, Integer progressCount, Integer endCount) {
            return GetEventDashboard.builder()
                    .recruitingCount(recruitingCount)
                    .progressCount(progressCount)
                    .endCount(endCount)
                    .build();
        }
    }


    @Builder
    @Getter
    public static class GetEventContactDto {
        private String phone;

        public static GetEventContactDto of(String phone) {
            return GetEventContactDto.builder()
                    .phone(phone)
                    .build();
        }
    }

}

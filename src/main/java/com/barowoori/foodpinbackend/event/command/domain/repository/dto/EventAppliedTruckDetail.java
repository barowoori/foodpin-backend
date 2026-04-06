package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplicationDate;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplicationStatus;
import com.barowoori.foodpinbackend.event.command.domain.model.EventDate;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentManager;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Getter
@SuperBuilder
public class EventAppliedTruckDetail extends TruckDetail {
    private String eventApplicationId;
    private String eventId;
    private Boolean isFullAttendanceRequired;
    private EventApplicationStatus status;
    private List<EventDetail.EventDateInfo> dates;

    public static EventAppliedTruckDetail of(EventApplication eventApplication, Truck truck, TruckDocumentManager truckDocumentManager, List<RegionCode> regions, String regionList, List<Category> categories, List<TruckMenu> truckMenus, ImageManager imageManager) {
        return EventAppliedTruckDetail.builder()
                .truck(TruckInfo.of(truck, imageManager))
                .documents(truckDocumentManager.getTypesForTruckDetail())
                .documentInfos(truckDocumentManager.getDocuments().stream().map(TruckDocumentInfo::of).toList())
                .businessRegistrationApproved(
                        truckDocumentManager.getDocuments() != null? truckDocumentManager.getDocuments().stream()
                                .filter(document -> document.getType().equals(DocumentType.BUSINESS_REGISTRATION))
                                .map(TruckDocument::getStatus)
                                .anyMatch(status -> status.equals(TruckDocumentStatus.APPROVED)) : Boolean.FALSE)
                .regions(regions)
                .categories(categories.stream()
                        .sorted(Comparator.comparing(Category::getCode))
                        .map(CategoryInfo::of).toList())
                .menus(truckMenus.stream()
                        .map(truckMenu -> MenuInfo.of(truckMenu, imageManager))
                        .toList())
                .eventApplicationId(eventApplication.getId())
                .eventId(eventApplication.getEvent().getId())
                .isFullAttendanceRequired(eventApplication.getEvent().getRecruitDetail().getIsFullAttendanceRequired())
                .status(eventApplication.getStatus())
                .dates(eventApplication.getSortedEventDates().stream()
                        .map(EventDateInfo::of).toList())
                .regionList(regionList)
                .build();
    }

    @Getter
    @Builder
    public static class EventDateInfo {
        private String id;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;

        public static EventDetail.EventDateInfo of(EventApplicationDate eventDate) {
            return EventDetail.EventDateInfo.builder()
                    .id(eventDate.getEventDate().getId())
                    .date(eventDate.getEventDate().getDate())
                    .startTime(eventDate.getEventDate().getStartTime())
                    .endTime(eventDate.getEventDate().getEndTime())
                    .build();
        }
    }
}

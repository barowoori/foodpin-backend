package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplicationDate;
import com.barowoori.foodpinbackend.event.command.domain.model.EventDate;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManagerRole;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentManager;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Getter
@SuperBuilder
public class EventAppliedTruckDetail extends TruckDetail {
    private String eventApplicationId;
    private List<LocalDate> dates;

    public static EventAppliedTruckDetail of(EventApplication eventApplication, Truck truck, TruckDocumentManager truckDocumentManager, List<RegionCode> regions, String regionList, List<Category> categories, List<TruckMenu> truckMenus, ImageManager imageManager) {
        return EventAppliedTruckDetail.builder()
                .truck(TruckInfo.of(truck, imageManager))
                .documents(truckDocumentManager.getTypes())
                .documentInfos(truckDocumentManager.getDocuments().stream().map(TruckDocumentInfo::of).toList())
                .regions(regions)
                .categories(categories.stream()
                        .sorted(Comparator.comparing(Category::getCode))
                        .map(CategoryInfo::of).toList())
                .menus(truckMenus.stream()
                        .map(truckMenu -> MenuInfo.of(truckMenu, imageManager))
                        .toList())
                .eventApplicationId(eventApplication.getId())
                .dates(eventApplication.getDates().stream()
                        .map(EventApplicationDate::getEventDate).map(EventDate::getDate).toList())
                .regionList(regionList)
                .build();
    }
}

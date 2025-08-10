package com.barowoori.foodpinbackend.event.command.domain.repository.dto;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
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
public class EventSelectedTruckDetail extends TruckDetail {
    private String eventApplicationId;
    private List<LocalDate> dates;

    public static EventSelectedTruckDetail of(EventTruck eventTruck, Truck truck, TruckDocumentManager truckDocumentManager, List<RegionCode> regions, List<Category> categories, List<TruckMenu> truckMenus, ImageManager imageManager){
        return EventSelectedTruckDetail.builder()
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
                .eventApplicationId(eventTruck.getId())
                .dates(eventTruck.getDates().stream()
                        .map(EventTruckDate::getEventDate).map(EventDate::getDate).toList())
                .build();
    }
}

package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.event.command.domain.exception.EventErrorCode;
import com.barowoori.foodpinbackend.event.command.domain.model.EventTruck;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventSelectedTruckDetail;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.repository.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentManager;
import com.barowoori.foodpinbackend.truck.query.application.TruckRegionFullNameGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class EventTruckDetailService {
    private final EventTruckRepository eventApplicationRepository;
    private final TruckDocumentRepository truckDocumentRepository;
    private final TruckMenuRepository truckMenuRepository;
    private final ImageManager imageManager;
    private final TruckCategoryRepository truckCategoryRepository;
    private final TruckRegionFullNameGenerator truckRegionFullNameGenerator;

    public EventTruckDetailService(EventTruckRepository eventApplicationRepository, TruckDocumentRepository truckDocumentRepository, TruckMenuRepository truckMenuRepository, ImageManager imageManager, TruckCategoryRepository truckCategoryRepository, TruckRegionFullNameGenerator truckRegionFullNameGenerator) {
        this.eventApplicationRepository = eventApplicationRepository;
        this.truckDocumentRepository = truckDocumentRepository;
        this.truckMenuRepository = truckMenuRepository;
        this.imageManager = imageManager;
        this.truckCategoryRepository = truckCategoryRepository;
        this.truckRegionFullNameGenerator = truckRegionFullNameGenerator;
    }

    @Transactional(readOnly = true)
    public EventSelectedTruckDetail getEventSelectedTruckInfo(String eventTruckId){
        EventTruck eventTruck = eventApplicationRepository.findById(eventTruckId)
                .orElseThrow(()-> new CustomException(EventErrorCode.EVENT_TRUCK_NOT_FOUND));
        Truck truck = eventTruck.getTruck();
        TruckDocumentManager documentManager = truckDocumentRepository.getDocumentManager(truck.getId());
        List<TruckMenu> truckMenus = truckMenuRepository.getMenuListWithPhotoByTruckId(truck.getId());
        List<RegionCode> regionNames = truckRegionFullNameGenerator.findRegionCodesByTruckId(truck.getId());
        List<Category> categories = truckCategoryRepository.findCategoriesByTruckId(truck.getId());

        return EventSelectedTruckDetail.of(eventTruck, truck, documentManager, regionNames, categories, truckMenus, imageManager);

    }
}

package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.event.command.domain.exception.EventErrorCode;
import com.barowoori.foodpinbackend.event.command.domain.model.EventApplication;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventAppliedTruckDetail;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.repository.TruckLikeRepository;
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
public class EventAppliedTruckDetailService {
    private final EventApplicationRepository eventApplicationRepository;
    private final TruckDocumentRepository truckDocumentRepository;
    private final TruckMenuRepository truckMenuRepository;
    private final ImageManager imageManager;
    private final TruckCategoryRepository truckCategoryRepository;
    private final TruckRegionFullNameGenerator truckRegionFullNameGenerator;

    public EventAppliedTruckDetailService(EventApplicationRepository eventApplicationRepository, TruckDocumentRepository truckDocumentRepository, TruckLikeRepository truckLikeRepository, TruckRepository truckRepository, TruckMenuRepository truckMenuRepository, TruckManagerRepository truckManagerRepository, ImageManager imageManager, TruckCategoryRepository truckCategoryRepository, TruckRegionFullNameGenerator truckRegionFullNameGenerator) {
        this.eventApplicationRepository = eventApplicationRepository;
        this.truckDocumentRepository = truckDocumentRepository;
        this.truckMenuRepository = truckMenuRepository;
        this.imageManager = imageManager;
        this.truckCategoryRepository = truckCategoryRepository;
        this.truckRegionFullNameGenerator = truckRegionFullNameGenerator;
    }

    @Transactional(readOnly = true)
    public EventAppliedTruckDetail getEventAppliedTruckDetail(String eventApplicationId) {
        EventApplication eventApplication = eventApplicationRepository.findById(eventApplicationId)
                .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_APPLICATION_NOT_FOUND));
        Truck truck = eventApplication.getTruck();
        TruckDocumentManager documentManager = truckDocumentRepository.getDocumentManager(truck.getId());
        List<TruckMenu> truckMenus = truckMenuRepository.getMenuListWithPhotoByTruckId(truck.getId());
        List<RegionCode> regionNames = truckRegionFullNameGenerator.findRegionCodesByTruckId(truck.getId());
        String regionList = truckRegionFullNameGenerator.makeRegionList(regionNames);
        List<Category> categories = truckCategoryRepository.findCategoriesByTruckId(truck.getId());

        return EventAppliedTruckDetail.of(eventApplication, truck, documentManager, regionNames, regionList, categories, truckMenus, imageManager);

    }
}

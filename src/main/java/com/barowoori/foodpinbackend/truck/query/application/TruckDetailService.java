package com.barowoori.foodpinbackend.truck.query.application;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.model.TruckLike;
import com.barowoori.foodpinbackend.member.command.domain.repository.TruckLikeRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.barowoori.foodpinbackend.truck.command.domain.exception.TruckErrorCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.repository.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TruckDetailService {
    private final TruckDocumentRepository truckDocumentRepository;
    private final TruckLikeRepository truckLikeRepository;
    private final TruckRegionRepository truckRegionRepository;
    private final TruckRepository truckRepository;
    private final TruckMenuRepository truckMenuRepository;
    private final TruckManagerRepository truckManagerRepository;
    private final ImageManager imageManager;
    private final TruckCategoryRepository truckCategoryRepository;
    private final TruckRegionFullNameGenerator truckRegionFullNameGenerator;

    public TruckDetailService(TruckDocumentRepository truckDocumentRepository, TruckLikeRepository truckLikeRepository, TruckRegionRepository truckRegionRepository,
                              TruckRepository truckRepository, TruckMenuRepository truckMenuRepository, TruckManagerRepository truckManagerRepository,
                              ImageManager imageManager, TruckCategoryRepository truckCategoryRepository, TruckRegionFullNameGenerator truckRegionFullNameGenerator) {
        this.truckDocumentRepository = truckDocumentRepository;
        this.truckLikeRepository = truckLikeRepository;
        this.truckRegionRepository = truckRegionRepository;
        this.truckRepository = truckRepository;
        this.truckMenuRepository = truckMenuRepository;
        this.truckManagerRepository = truckManagerRepository;
        this.imageManager = imageManager;
        this.truckCategoryRepository = truckCategoryRepository;
        this.truckRegionFullNameGenerator = truckRegionFullNameGenerator;
    }

    @Transactional
    public TruckDetail getTruckDetail(String memberId, String truckId) {
        Truck truck = truckRepository.getTruckWithPhotoById(truckId);
        if (truck == null || truck.getIsDeleted()) {
            throw new CustomException(TruckErrorCode.NOT_FOUND_TRUCK);
        }
        TruckDocumentManager documentManager = truckDocumentRepository.getDocumentManager(truckId);
        List<TruckMenu> truckMenus = truckMenuRepository.getMenuListWithPhotoByTruckId(truckId);
        TruckLike truckLike = truckLikeRepository.findByMemberIdAndTruckId(memberId, truckId);
        TruckManager truckManager = truckManagerRepository.findByTruckIdAndMemberId(truckId, memberId);
        List<RegionCode> regionNames = truckRegionFullNameGenerator.findRegionCodesByTruckId(truckId);
        List<Category> categories = truckCategoryRepository.findCategoriesByTruckId(truckId);
        truck.addViews();
        return TruckDetail.of(truckManager, truck, documentManager, regionNames, categories, truckMenus, truckLike != null, imageManager);
    }
}

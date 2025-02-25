package com.barowoori.foodpinbackend.truck.query.application;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.model.TruckLike;
import com.barowoori.foodpinbackend.member.command.domain.repository.TruckLikeRepository;
import com.barowoori.foodpinbackend.truck.command.domain.exception.TruckErrorCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;
import com.barowoori.foodpinbackend.truck.command.domain.repository.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDocumentManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public TruckDetailService(TruckDocumentRepository truckDocumentRepository, TruckLikeRepository truckLikeRepository, TruckRegionRepository truckRegionRepository, TruckRepository truckRepository, TruckMenuRepository truckMenuRepository, TruckManagerRepository truckManagerRepository, ImageManager imageManager) {
        this.truckDocumentRepository = truckDocumentRepository;
        this.truckLikeRepository = truckLikeRepository;
        this.truckRegionRepository = truckRegionRepository;
        this.truckRepository = truckRepository;
        this.truckMenuRepository = truckMenuRepository;
        this.truckManagerRepository = truckManagerRepository;
        this.imageManager = imageManager;
    }

    // 사진들 돌려줄 때 file id도 줘야 할 듯..? + 카테고리 추가
    @Transactional(readOnly = true)
    public TruckDetail getTruckDetail(String memberId, String truckId) {
        Truck truck = truckRepository.getTruckWithPhotoById(truckId);
        if (truck == null) {
            throw new CustomException(TruckErrorCode.NOT_FOUND_TRUCK);
        }
        TruckDocumentManager documentManager = truckDocumentRepository.getDocumentManager(truckId);
        List<TruckMenu> truckMenus = truckMenuRepository.getMenuListWithPhotoByTruckId(truckId);
        TruckLike truckLike = truckLikeRepository.findByMemberIdAndTruckId(memberId, truckId);
        TruckManager truckManager = truckManagerRepository.findByTruckIdAndMemberId(truckId, memberId);
        List<String> regionNames = truckRegionRepository.findRegionNamesByTruckId(truckId);
        return TruckDetail.of(truckManager, truck, documentManager.getTypes(), regionNames, truckMenus, truckLike != null, imageManager);
    }
}

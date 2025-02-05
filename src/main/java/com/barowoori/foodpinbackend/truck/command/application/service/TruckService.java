package com.barowoori.foodpinbackend.truck.command.application.service;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.repository.FileRepository;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.file.infra.domain.ImageDirectory;
import com.barowoori.foodpinbackend.truck.command.application.dto.RequestTruck;
import com.barowoori.foodpinbackend.truck.command.domain.exception.TruckErrorCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TruckService {
    private final TruckRepository truckRepository;
    private final TruckPhotoRepository truckPhotoRepository;
    private final TruckRegionRepository truckRegionRepository;
    private final CategoryRepository categoryRepository;
    private final TruckCategoryRepository truckCategoryRepository;
    private final TruckMenuRepository truckMenuRepository;
    private final TruckMenuPhotoRepository truckMenuPhotoRepository;
    private final TruckDocumentRepository truckDocumentRepository;
    private final FileRepository fileRepository;

    private String getMemberId(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Transactional
    public void createTruck(RequestTruck.CreateTruckDto createTruckDto){
        String memberId = getMemberId();

        // 트럭 생성
        Truck truck = createTruckDto.getTruckInfoDto().toEntity();
        truckRepository.save(truck);
        // 트럭 사진 생성
        for (String fileId : createTruckDto.getTruckInfoDto().getFileIdList()){
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_PHOTO_NOT_FOUND));
            TruckPhoto truckPhoto = TruckPhoto.builder().file(file).updatedBy(memberId).truck(truck).build();
            truckPhotoRepository.save(truckPhoto);
        }

        // 트럭 지역 생성
        TruckRegion truckRegion = createTruckDto.getTruckRegionDto().toEntity(truck);
        truckRegionRepository.save(truckRegion);

        // 트럭 카테고리 생성
        createTruckDto.getTruckCategoryDtoSet().forEach(truckCategoryDto -> {
            Category category = categoryRepository.findById(truckCategoryDto.getCategoryId()).get();
            TruckCategory truckCategory = truckCategoryDto.toEntity(truck, category);
            truckCategoryRepository.save(truckCategory);
        });

        // 트럭 메뉴 생성
        for (RequestTruck.TruckMenuDto truckMenuDto : createTruckDto.getTruckMenuDtoList()) {
            TruckMenu truckMenu = truckMenuDto.toEntity(truck);
            truckMenuRepository.save(truckMenu);
            // 트럭 메뉴 사진 생성
            if (!truckMenuDto.getFileIdList().isEmpty()) {
                for (String fileId : truckMenuDto.getFileIdList()) {
                    File file = fileRepository.findById(fileId)
                            .orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_MENU_PHOTO_NOT_FOUND));
                    TruckMenuPhoto truckMenuPhoto = TruckMenuPhoto.builder().file(file).updatedBy(memberId).truckMenu(truckMenu).build();
                    truckMenuPhotoRepository.save(truckMenuPhoto);
                }
            }
        }

        // 트럭 문서 생성 및 사진 저장
        // 사진 한 장만 들어감, 클라이언트에서 여러 개 보내면 개수만큼 TruckDocument 엔티티가 만들어짐
        // -> TruckDocument 생성자에서 path 제거 후, 여러 장 저장하려면 List<TruckDocumentPhoto> 생성 필요할 듯
        for (RequestTruck.TruckDocumentDto truckDocumentDto : createTruckDto.getTruckDocumentDtoSet()) {
            if (!truckDocumentDto.getFileIdList().isEmpty()) {
                for (String fileId : truckDocumentDto.getFileIdList()) {
                    File file = fileRepository.findById(fileId)
                            .orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_DOCUMENT_PHOTO_NOT_FOUND));
                    TruckDocument truckDocument = truckDocumentDto.toEntity(memberId, file.getPath(), truck);
                    truckDocumentRepository.save(truckDocument);
                }
            } else
                throw new CustomException(TruckErrorCode.TRUCK_DOCUMENT_PHOTO_EMPTY);
        }

    }
}

package com.barowoori.foodpinbackend.truck.command.application.service;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.repository.FileRepository;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
import com.barowoori.foodpinbackend.truck.command.application.dto.RequestTruck;
import com.barowoori.foodpinbackend.truck.command.domain.exception.TruckErrorCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckManagerSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TruckService {
    private final MemberRepository memberRepository;
    private final TruckRepository truckRepository;
    private final TruckPhotoRepository truckPhotoRepository;
    private final TruckRegionRepository truckRegionRepository;
    private final CategoryRepository categoryRepository;
    private final TruckCategoryRepository truckCategoryRepository;
    private final TruckMenuRepository truckMenuRepository;
    private final TruckMenuPhotoRepository truckMenuPhotoRepository;
    private final TruckDocumentRepository truckDocumentRepository;
    private final TruckManagerRepository truckManagerRepository;
    private final FileRepository fileRepository;
    private final RegionDoRepository regionDoRepository;
    private final ImageManager imageManager;

    private String getMemberId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Truck getTruck(String truckId) {
        return truckRepository.findById(truckId)
                .orElseThrow(() -> new CustomException(TruckErrorCode.NOT_FOUND_TRUCK));
    }

    @Transactional
    public void createTruck(RequestTruck.CreateTruckDto createTruckDto) {
        String memberId = getMemberId();

        // 트럭 생성
        Truck truck = createTruckDto.getTruckInfoDto().toEntity(memberId);
        truckRepository.save(truck);
        // 트럭 사진 생성
        for (String fileId : createTruckDto.getTruckInfoDto().getFileIdList()) {
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_PHOTO_NOT_FOUND));
            TruckPhoto truckPhoto = TruckPhoto.builder().file(file).updatedBy(memberId).truck(truck).build();
            truckPhotoRepository.save(truckPhoto);
        }

        // 트럭 지역 생성
        createTruckDto.getTruckRegionDtoSet().forEach(truckRegionDto -> {
            RegionInfo regionInfo = regionDoRepository.findByCode(truckRegionDto.getRegionCode());
            TruckRegion truckRegion = truckRegionDto.toEntity(truck, regionInfo);
            truckRegionRepository.save(truckRegion);
        });

        // 트럭 카테고리 생성
        createTruckDto.getTruckCategoryDtoSet().forEach(truckCategoryDto -> {
            Category category = categoryRepository.findByCode(truckCategoryDto.getCategoryCode());
            if (category == null) {
                throw new CustomException(TruckErrorCode.CATEGORY_NOT_FOUND);
            }
            TruckCategory truckCategory = truckCategoryDto.toEntity(truck, category);
            truckCategoryRepository.save(truckCategory);
        });

        // 트럭 메뉴 생성
        for (RequestTruck.TruckMenuDto truckMenuDto : createTruckDto.getTruckMenuDtoList()) {
            TruckMenu truckMenu = truckMenuDto.toEntity(truck);
            truckMenuRepository.save(truckMenu);
            // 트럭 메뉴 사진 생성
            if (!Objects.equals(truckMenuDto.getFileIdList(), null) && !truckMenuDto.getFileIdList().isEmpty()) {
                for (String fileId : truckMenuDto.getFileIdList()) {
                    File file = fileRepository.findById(fileId)
                            .orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_MENU_PHOTO_NOT_FOUND));
                    TruckMenuPhoto truckMenuPhoto = TruckMenuPhoto.builder().file(file).updatedBy(memberId).truckMenu(truckMenu).build();
                    truckMenuPhotoRepository.save(truckMenuPhoto);
                }
            }
        }

        //TODO 트럭 문서 생성 및 사진 저장 검토 필요해보입니다(기획서, 디자인안 참고)
        // 사업자등록증은 사진이 없고, 그 외 서류들은 사진만 있는 것이 맞는 지, 맞다면 현 구조 그대로 유지해도 괜찮은 지가 궁금합니다
        // 그리고 현재 상태 TruckDocument에 사진을 어떻게 저장해야 할 지, documentId는 어떤 것인지 잘 모르겠습니다..
        // 일단 지금 코드 구조 이해를 잘 못 하고 있는 것 같아서 바로 밑에 서비스 로직 완성이나 서류 변경/등록 api 생성은 보류했습니다
        if (!Objects.equals(createTruckDto.getTruckDocumentDtoSet(), null) && !createTruckDto.getTruckDocumentDtoSet().isEmpty()) {
            for (RequestTruck.TruckDocumentDto truckDocumentDto : createTruckDto.getTruckDocumentDtoSet()) {
                TruckDocument truckDocument = truckDocumentDto.toEntity(memberId, truck);
                truckDocumentRepository.save(truckDocument);
                if (!Objects.equals(truckDocumentDto.getFileIdList(), null) && !truckDocumentDto.getFileIdList().isEmpty()) {
                    for (String fileId : truckDocumentDto.getFileIdList()) {
                        File file = fileRepository.findById(fileId)
                                .orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_DOCUMENT_PHOTO_NOT_FOUND));
                        //저장할 곳이 없음
                    }
                }
            }
        }

        // 트럭 관리자 생성
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        TruckManager truckManager = TruckManager.builder()
                .role(TruckManagerRole.OWNER)
                .roleUpdatedAt(LocalDateTime.now())
                .member(member)
                .truck(truck)
                .build();
        truckManagerRepository.save(truckManager);
    }

    @Transactional
    public void addManager(String truckId) {
        String memberId = getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        Truck truck = getTruck(truckId);

        TruckManager truckManager = truckManagerRepository.findByTruckIdAndMemberId(truckId, memberId);
        if (truckManager == null) {
            TruckManager newTruckManager = TruckManager.builder()
                    .role(TruckManagerRole.MEMBER)
                    .roleUpdatedAt(LocalDateTime.now())
                    .member(member)
                    .truck(truck)
                    .build();
            truckManagerRepository.save(newTruckManager);
        } else throw new CustomException(TruckErrorCode.TRUCK_MANAGER_EXISTS);
    }

    @Transactional
    public void updateTruckInfo(String truckId, RequestTruck.UpdateTruckInfoDto updateTruckInfoDto) {
        String memberId = getMemberId();
        Truck truck = getTruck(truckId);
        truck.update(updateTruckInfoDto.getName(), memberId, updateTruckInfoDto.getDescription(), truck.getElectricityUsage(), truck.getGasUsage(), truck.getSelfGenerationAvailability());
        truckRepository.save(truck);

        // truckPhoto id가 아닌 file id로 수정 돌아감
        List<TruckPhoto> photoList = truckPhotoRepository.findByTruckOrderByCreateAt(truck);
        photoList.forEach(truckPhotoRepository::delete);
        for (String fileId : updateTruckInfoDto.getFileIdList()) {
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_PHOTO_NOT_FOUND));
            TruckPhoto truckPhoto = TruckPhoto.builder().file(file).updatedBy(memberId).truck(truck).build();
            truckPhotoRepository.save(truckPhoto);
        }
    }

    @Transactional
    public void updateTruckOperation(String truckId, RequestTruck.UpdateTruckOperationDto updateTruckOperationDto) {
        String memberId = getMemberId();
        Truck truck = getTruck(truckId);
        truck.update(truck.getName(), memberId, truck.getDescription(), updateTruckOperationDto.getElectricityUsage(), updateTruckOperationDto.getGasUsage(), updateTruckOperationDto.getSelfGenerationAvailability());
        truckRepository.save(truck);

        List<TruckRegion> truckRegionList = truckRegionRepository.findAllByTruck(truck);
        truckRegionList.forEach(truckRegionRepository::delete);
        updateTruckOperationDto.getTruckRegionDtoSet().forEach(truckRegionDto -> {
            RegionInfo regionInfo = regionDoRepository.findByCode(truckRegionDto.getRegionCode());
            TruckRegion truckRegion = TruckRegion.builder()
                    .truck(truck)
                    .regionType(regionInfo.getRegionType())
                    .regionId(regionInfo.getRegionId())
                    .build();
            truckRegionRepository.save(truckRegion);
        });
    }

    @Transactional
    public void updateTruckMenu(String truckId, RequestTruck.UpdateTruckMenuDto updateTruckMenuDto) {
        String memberId = getMemberId();
        Truck truck = getTruck(truckId);

        List<TruckCategory> truckCategoryList = truckCategoryRepository.findAllByTruck(truck);
        truckCategoryList.forEach(truckCategoryRepository::delete);
        updateTruckMenuDto.getTruckCategoryDtoSet().forEach(truckCategoryDto -> {
            Category category = categoryRepository.findByCode(truckCategoryDto.getCategoryCode());
            if (category == null) {
                throw new CustomException(TruckErrorCode.CATEGORY_NOT_FOUND);
            }
            TruckCategory truckCategory = truckCategoryDto.toEntity(truck, category);
            truckCategoryRepository.save(truckCategory);
        });

        List<TruckMenu> truckMenuList = truckMenuRepository.getMenuListWithPhotoByTruckId(truckId);
        truckMenuList.forEach(truckMenu -> {
            List<TruckMenuPhoto> truckMenuPhotoList = truckMenuPhotoRepository.findAllByTruckMenu(truckMenu);
            truckMenuPhotoList.forEach(truckMenuPhotoRepository::delete);
            truckMenuRepository.delete(truckMenu);
        });

        for (RequestTruck.TruckMenuDto truckMenuDto : updateTruckMenuDto.getTruckMenuDtoList()) {
            TruckMenu truckMenu = truckMenuDto.toEntity(truck);
            truckMenuRepository.save(truckMenu);

            if (!Objects.equals(truckMenuDto.getFileIdList(), null) && !truckMenuDto.getFileIdList().isEmpty()) {
                for (String fileId : truckMenuDto.getFileIdList()) {
                    File file = fileRepository.findById(fileId)
                            .orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_MENU_PHOTO_NOT_FOUND));
                    TruckMenuPhoto truckMenuPhoto = TruckMenuPhoto.builder().file(file).updatedBy(memberId).truckMenu(truckMenu).build();
                    truckMenuPhotoRepository.save(truckMenuPhoto);
                }
            }
        }
    }

    //TODO 트럭 문서 쪽(생성) 정리되는대로 바로 생성
//    @Transactional
//    public void setTruckDocument()

    @Transactional
    public void changeOwner(String managerId, String truckId) {
        String memberId = getMemberId();

        TruckManager truckOwner = truckManagerRepository.findByTruckIdAndMemberId(truckId, memberId);
        TruckManager truckMember = truckManagerRepository.findByTruckIdAndMemberId(truckId, managerId);
        if (truckOwner != null && truckMember != null && Objects.equals(truckOwner.getRole(), TruckManagerRole.OWNER)) {
            truckMember.updateRole(TruckManagerRole.OWNER);
            truckManagerRepository.save(truckMember);
            truckOwner.updateRole(TruckManagerRole.MEMBER);
            truckManagerRepository.save(truckOwner);
        } else throw new CustomException(TruckErrorCode.TRUCK_OWNER_NOT_FOUND);
    }

    @Transactional
    public void deleteManager(String managerId, String truckId) {
        String memberId = getMemberId();

        TruckManager truckOwner = truckManagerRepository.findByTruckIdAndMemberId(truckId, memberId);
        TruckManager truckMember = truckManagerRepository.findByTruckIdAndMemberId(truckId, managerId);
        if (truckOwner != null && truckMember != null && Objects.equals(truckOwner.getRole(), TruckManagerRole.OWNER)) {
            truckManagerRepository.delete(truckMember);
        } else throw new CustomException(TruckErrorCode.TRUCK_OWNER_NOT_FOUND);
    }

    @Transactional
    public void deleteTruck(String truckId) {
        TruckManager truckManager = truckManagerRepository.findByTruckIdAndMemberId(truckId, getMemberId());
        if (truckManager != null && Objects.equals(truckManager.getRole(), TruckManagerRole.OWNER)) {
            Truck truck = getTruck(truckId);
            truck.delete();
        } else throw new CustomException(TruckErrorCode.TRUCK_OWNER_NOT_FOUND);
    }

    @Transactional(readOnly = true)
    public Page<TruckManagerSummary> getTruckManagerList(String truckId, Pageable pageable) {
        Page<TruckManagerSummary> truckManagerSummaries = truckManagerRepository.findTruckManagerPages(truckId, getMemberId(), pageable);
        return truckManagerSummaries.map(truckManagerSummary -> truckManagerSummary.convertToPreSignedUrl(imageManager));
    }
}
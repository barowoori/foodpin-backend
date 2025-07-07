package com.barowoori.foodpinbackend.truck.command.application.service;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.document.command.domain.repository.BusinessRegistrationRepository;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.repository.FileRepository;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.truck.ManagerAddedNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.truck.ManagerRemovedNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.truck.OwnerUpdatedNotificationEvent;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionDo;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionGu;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionGun;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionSi;
import com.barowoori.foodpinbackend.region.command.domain.query.application.RegionSearchProcessor;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGuRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGunRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionSiRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
import com.barowoori.foodpinbackend.truck.command.application.dto.RequestTruck;
import com.barowoori.foodpinbackend.truck.command.application.dto.ResponseTruck;
import com.barowoori.foodpinbackend.truck.command.domain.exception.TruckErrorCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckManagerSummary;
import com.barowoori.foodpinbackend.truck.command.domain.service.TruckManagerInvitationGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final TruckDocumentPhotoRepository truckDocumentPhotoRepository;
    private final BusinessRegistrationRepository businessRegistrationRepository;
    private final TruckManagerInvitationGenerator truckManagerInvitationGenerator;
    private final RegionSiRepository regionSiRepository;
    private final RegionGuRepository regionGuRepository;
    private final RegionGunRepository regionGunRepository;

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

        List<RegionDo> regionDos = regionDoRepository.findAll();
        List<RegionSi> regionSis = regionSiRepository.findAll();
        List<RegionGu> regionGus = regionGuRepository.findAll();
        List<RegionGun> regionGuns = regionGunRepository.findAll();

        RegionSearchProcessor regionSearchProcessor = new RegionSearchProcessor(regionDos, regionSis, regionGus, regionGuns);

        // 트럭 지역 생성
        List<TruckRegion> truckRegions = createTruckDto.getTruckRegionCodeSet().stream()
                .map(truckRegionCode -> {
                    RegionInfo regionInfo = regionSearchProcessor.findByCode(truckRegionCode);
                    return TruckRegion.builder()
                            .regionType(regionInfo.getRegionType())
                            .regionId(regionInfo.getRegionId())
                            .truck(truck)
                            .build();
                })
                .toList();
        truckRegionRepository.saveAll(truckRegions);

        // 트럭 카테고리 생성
        createTruckDto.getTruckCategoryCodeSet().forEach(truckCategoryCode -> {
            Category category = categoryRepository.findByCode(truckCategoryCode);
            if (category == null) {
                throw new CustomException(TruckErrorCode.CATEGORY_NOT_FOUND);
            }
            TruckCategory truckCategory = TruckCategory.builder()
                    .truck(truck)
                    .category(category)
                    .build();
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

        if (!Objects.equals(createTruckDto.getTruckDocumentDtoSet(), null) && !createTruckDto.getTruckDocumentDtoSet().isEmpty()) {
            for (RequestTruck.TruckDocumentDto truckDocumentDto : createTruckDto.getTruckDocumentDtoSet()) {
                if (truckDocumentDto.getType().equals(DocumentType.BUSINESS_REGISTRATION) && !Objects.equals(truckDocumentDto.getCreateBusinessRegistrationDto(), null)) {
                    BusinessRegistration businessRegistration = truckDocumentDto.getCreateBusinessRegistrationDto().toEntity(memberId);
                    businessRegistration = businessRegistrationRepository.save(businessRegistration);
                    TruckDocument truckDocument = truckDocumentDto.toEntity(memberId, businessRegistration.getId(), truck);
                    truckDocumentRepository.save(truckDocument);
                } else throw new CustomException(TruckErrorCode.BUSINESS_INFO_MISSED);
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
    public void addManager(RequestTruck.AddManagerDto addManagerDto) {
        String memberId = getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        Truck truck = getTruck(addManagerDto.getTruckId());
        if (!truckManagerInvitationGenerator.matchInvitationCode(truck, addManagerDto.getCode()))
            throw new CustomException(TruckErrorCode.INCORRECT_INVITATION_CODE);

        TruckManager truckManager = truckManagerRepository.findByTruckIdAndMemberId(addManagerDto.getTruckId(), memberId);
        if (truckManager == null) {
            TruckManager newTruckManager = TruckManager.builder()
                    .role(TruckManagerRole.MEMBER)
                    .roleUpdatedAt(LocalDateTime.now())
                    .member(member)
                    .truck(truck)
                    .build();
            truckManagerRepository.save(newTruckManager);
        } else throw new CustomException(TruckErrorCode.TRUCK_MANAGER_EXISTS);
        NotificationEvent.raise(new ManagerAddedNotificationEvent(truck.getId(), truck.getName(), member.getNickname()));
    }

    @Transactional(readOnly = true)
    public ResponseTruck.GetTruckInviteMessageDto getManagerInviteMessage(String truckId) {
        Truck truck = getTruck(truckId);
        return ResponseTruck.GetTruckInviteMessageDto.builder()
                .message(truckManagerInvitationGenerator.getMessage(truck))
                .build();
    }

    @Transactional
    public void updateTruckInfo(String truckId, RequestTruck.UpdateTruckInfoDto updateTruckInfoDto) {
        String memberId = getMemberId();
        Truck truck = getTruck(truckId);
        truck.update(updateTruckInfoDto.getName(), memberId, updateTruckInfoDto.getDescription(), truck.getElectricityUsage(), truck.getGasUsage(), truck.getSelfGenerationAvailability());
        truckRepository.save(truck);

        List<TruckPhoto> photoList = truckPhotoRepository.findByTruckOrderByCreateAt(truck);
        photoList.forEach(truckPhotoRepository::delete);
        truckRepository.flush();
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
        updateTruckOperationDto.getTruckRegionCodeSet().forEach(truckRegionCode -> {
            RegionInfo regionInfo = regionDoRepository.findByCode(truckRegionCode);
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
        updateTruckMenuDto.getTruckCategoryCodeSet().forEach(truckCategoryCode -> {
            Category category = categoryRepository.findByCode(truckCategoryCode);
            if (category == null) {
                throw new CustomException(TruckErrorCode.CATEGORY_NOT_FOUND);
            }
            TruckCategory truckCategory = TruckCategory.builder()
                    .truck(truck)
                    .category(category)
                    .build();
            truckCategoryRepository.save(truckCategory);
        });

        List<TruckMenu> truckMenuList = truckMenuRepository.getMenuListWithPhotoByTruckId(truckId);
        truckMenuList.forEach(truckMenu -> {
            List<TruckMenuPhoto> truckMenuPhotoList = truckMenuPhotoRepository.findAllByTruckMenu(truckMenu);
            if (truckMenuPhotoList != null) {
                truckMenuPhotoList.forEach(truckMenuPhotoRepository::delete);
            }
            truckMenuRepository.delete(truckMenu);
        });
        truckMenuPhotoRepository.flush();
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

    @Transactional
    public void setTruckDocument(String truckId, RequestTruck.TruckDocumentDto truckDocumentDto) {
        String memberId = getMemberId();
        Truck truck = getTruck(truckId);

        if (truckDocumentDto.getType().equals(DocumentType.BUSINESS_REGISTRATION)) {
            if (!Objects.equals(truckDocumentDto.getCreateBusinessRegistrationDto(), null)) {

                TruckDocument originalTruckDocument = truckDocumentRepository.findByTruckIdAndType(truckId, truckDocumentDto.getType());
                if (originalTruckDocument == null) {
                    BusinessRegistration businessRegistration = truckDocumentDto.getCreateBusinessRegistrationDto().toEntity(memberId);
                    businessRegistration = businessRegistrationRepository.save(businessRegistration);
                    TruckDocument truckDocument = truckDocumentDto.toEntity(memberId, businessRegistration.getId(), truck);
                    truckDocumentRepository.save(truckDocument);
                } else {
                    BusinessRegistration businessRegistration = truckDocumentRepository.getBusinessRegistrationDocumentByTruckId(truckId);
                    businessRegistration.update(memberId, truckDocumentDto.getCreateBusinessRegistrationDto().getBusinessNumber(), truckDocumentDto.getCreateBusinessRegistrationDto().getBusinessName(),
                            truckDocumentDto.getCreateBusinessRegistrationDto().getRepresentativeName(), truckDocumentDto.getCreateBusinessRegistrationDto().getOpeningDate());
                    businessRegistrationRepository.save(businessRegistration);
                    originalTruckDocument.update(LocalDateTime.now(), memberId, truckDocumentDto.getApproval());
                    truckDocumentRepository.save(originalTruckDocument);
                }
            } else throw new CustomException(TruckErrorCode.BUSINESS_INFO_MISSED);
        } else if (!Objects.equals(truckDocumentDto.getFileIdList(), null) && !truckDocumentDto.getFileIdList().isEmpty()) {

            TruckDocument originalTruckDocument = truckDocumentRepository.findByTruckIdAndType(truckId, truckDocumentDto.getType());
            if (originalTruckDocument == null) {
                TruckDocument truckDocument = truckDocumentDto.toEntity(memberId, truck);
                truckDocumentRepository.save(truckDocument);

                for (String fileId : truckDocumentDto.getFileIdList()) {
                    File file = fileRepository.findById(fileId)
                            .orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_DOCUMENT_PHOTO_NOT_FOUND));
                    TruckDocumentPhoto truckDocumentPhoto = TruckDocumentPhoto.builder().file(file).updatedBy(memberId).truckDocument(truckDocument).build();
                    truckDocumentPhotoRepository.save(truckDocumentPhoto);
                }
            } else {
                List<TruckDocumentPhoto> truckDocumentPhotoList = truckDocumentPhotoRepository.findByTruckDocumentId(originalTruckDocument.getId());
                truckDocumentPhotoList.forEach(truckDocumentPhotoRepository::delete);

                for (String fileId : truckDocumentDto.getFileIdList()) {
                    File file = fileRepository.findById(fileId)
                            .orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_DOCUMENT_PHOTO_NOT_FOUND));
                    TruckDocumentPhoto truckDocumentPhoto = TruckDocumentPhoto.builder().file(file).updatedBy(memberId).truckDocument(originalTruckDocument).build();
                    truckDocumentPhotoRepository.save(truckDocumentPhoto);
                }
                originalTruckDocument.update(LocalDateTime.now(), memberId, truckDocumentDto.getApproval());
                truckDocumentRepository.save(originalTruckDocument);
            }
        } else throw new CustomException(TruckErrorCode.DOCUMENT_PHOTO_MISSED);
    }

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
        Truck truck = truckMember.getTruck();
        NotificationEvent.raise(new OwnerUpdatedNotificationEvent(truckId, truck.getName(), truckMember.getMember().getNickname()));
    }

    @Transactional
    public void deleteManager(String managerId, String truckId) {
        String memberId = getMemberId();

        TruckManager truckOwner = truckManagerRepository.findByTruckIdAndMemberId(truckId, memberId);
        TruckManager truckMember = truckManagerRepository.findByTruckIdAndMemberId(truckId, managerId);
        if (truckOwner != null && truckMember != null && Objects.equals(truckOwner.getRole(), TruckManagerRole.OWNER)) {
            truckManagerRepository.delete(truckMember);
        } else throw new CustomException(TruckErrorCode.TRUCK_OWNER_NOT_FOUND);
        Truck truck = truckMember.getTruck();

        NotificationEvent.raise(new ManagerRemovedNotificationEvent(truck.getName(), managerId));
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

    @Transactional(readOnly = true)
    public ResponseTruck.GetBusinessRegistrationInfo getTruckBusinessRegistrationDocumentInfo(String truckId) {
        Truck truck = getTruck(truckId);
        BusinessRegistration businessRegistration = truckDocumentRepository.getBusinessRegistrationDocumentByTruckId(truck.getId());
        return ResponseTruck.GetBusinessRegistrationInfo.of(businessRegistration);
    }
}
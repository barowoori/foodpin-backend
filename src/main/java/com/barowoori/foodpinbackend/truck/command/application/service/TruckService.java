package com.barowoori.foodpinbackend.truck.command.application.service;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.common.exception.WithdrawalBlockHeaders;
import com.barowoori.foodpinbackend.document.command.domain.model.BusinessRegistration;
import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.document.command.domain.repository.BusinessRegistrationRepository;
import com.barowoori.foodpinbackend.event.command.application.service.EventService;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.event.command.domain.service.EventDateCalculator;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.repository.FileRepository;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.model.TruckLike;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.TruckLikeRepository;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.SelectionCanceledNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.truck.*;
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
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.BackOfficeTruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckManagerSummary;
import com.barowoori.foodpinbackend.truck.command.domain.service.TruckContactAccessLogService;
import com.barowoori.foodpinbackend.truck.command.domain.service.TruckManagerInvitationGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private final TruckDocumentPhotoRepository truckDocumentPhotoRepository;
    private final BusinessRegistrationRepository businessRegistrationRepository;
    private final TruckManagerInvitationGenerator truckManagerInvitationGenerator;
    private final RegionSiRepository regionSiRepository;
    private final RegionGuRepository regionGuRepository;
    private final RegionGunRepository regionGunRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final EventService eventService;
    private final TruckLikeRepository truckLikeRepository;
    private final EventTruckRepository eventTruckRepository;
    private final TruckContactAccessLogService truckContactAccessLogService;

    private String getMemberId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Truck getTruck(String truckId) {
        return truckRepository.findById(truckId)
                .orElseThrow(() -> new CustomException(TruckErrorCode.NOT_FOUND_TRUCK));
    }

    private Truck validateNotDeletedTruck(String truckId) {
        Truck truck = truckRepository.findByIdAndIsDeleted(truckId, Boolean.FALSE);
        if (truck == null) {
            throw new CustomException(TruckErrorCode.NOT_FOUND_TRUCK);
        }
        return truck;
    }

    private TruckDocument getTruckDocument(String truckId, DocumentType documentType) {
        TruckDocument truckDocument = truckDocumentRepository.findByTruckIdAndType(truckId, documentType);
        if (truckDocument == null) {
            throw new CustomException(TruckErrorCode.TRUCK_DOCUMENT_NOT_FOUND);
        }
        return truckDocument;
    }

    private void validateTruckAccess(String truckId, String memberId) {
        TruckManager truckManager = truckManagerRepository.findByTruckIdAndMemberId(truckId, memberId);
        if (truckManager == null) {
            throw new CustomException(TruckErrorCode.TRUCK_MANAGER_NOT_FOUND);
        }
    }

    private void validateManagedTruckLimit(String memberId) {
        long managedTruckCount = truckManagerRepository.countByMemberIdAndTruckIsDeletedFalse(memberId);
        if (managedTruckCount >= 100) {
            throw new CustomException(TruckErrorCode.TRUCK_MANAGER_LIMIT_EXCEEDED);
        }
    }

    private void validateTruckUpdatable(String truckId) {
        boolean hasPendingApplication = Boolean.TRUE.equals(eventApplicationRepository.existsPendingApplicationByTruckId(truckId));
        boolean hasPendingEventTruck = Boolean.TRUE.equals(eventTruckRepository.existsPendingEventTruckByTruckId(truckId));
        boolean hasConfirmedProgressEventTruck = Boolean.TRUE.equals(eventTruckRepository.existsConfirmedProgressEventTruckByTruckId(truckId));

        if (hasPendingApplication || hasPendingEventTruck || hasConfirmedProgressEventTruck) {
            throw new CustomException(TruckErrorCode.TRUCK_UPDATE_NOT_AVAILABLE);
        }
    }

    @Transactional
    public void createTruck(RequestTruck.CreateTruckDto createTruckDto) {
        String memberId = getMemberId();
        validateManagedTruckLimit(memberId);

        // 트럭 생성
        Truck truck = createTruckDto.getTruckInfoDto().toEntity(memberId);

        List<Integer> createdMenuPrices = createTruckDto.getTruckMenuDtoList().stream()
                .map(RequestTruck.TruckMenuDto::getPrice)
                .toList();
        truck.updateAvgMenuPrice(createdMenuPrices);

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
            TruckCategory truckCategory = new TruckCategory(truck, category);
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
                    if (truckDocumentDto.getFileIdList() != null && !truckDocumentDto.getFileIdList().isEmpty()) {
                        saveTruckDocumentPhotos(truckDocumentDto.getFileIdList(), truckDocument, memberId);
                    }
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
            validateManagedTruckLimit(memberId);
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
        validateTruckAccess(truckId, getMemberId());
        Truck truck = getTruck(truckId);
        return ResponseTruck.GetTruckInviteMessageDto.builder()
                .message(truckManagerInvitationGenerator.getMessage(truck))
                .build();
    }

    @Transactional
    public void updateTruckInfo(String truckId, RequestTruck.UpdateTruckInfoDto updateTruckInfoDto) {
        String memberId = getMemberId();
        Truck truck = getTruck(truckId);
        validateTruckAccess(truck.getId(), memberId);
        truck.updateBasicInfo(
                updateTruckInfoDto.getName(),
                memberId,
                updateTruckInfoDto.getDescription(),
                updateTruckInfoDto.getTruckColors(),
                updateTruckInfoDto.getBodyType()
        );
        truckRepository.save(truck);

        List<TruckPhoto> photoList = truckPhotoRepository.findByTruckOrderByCreateAt(truck);
        photoList.forEach(truckPhotoRepository::delete);
        truckPhotoRepository.flush();
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
        validateTruckAccess(truck.getId(), memberId);
        truck.updateOperationInfo(
                memberId,
                updateTruckOperationDto.getElectricityUsage(),
                updateTruckOperationDto.getGasUsage(),
                updateTruckOperationDto.getSelfGenerationAvailability(),
                updateTruckOperationDto.getIsCatering()
        );
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
        validateTruckAccess(truck.getId(), memberId);
        validateTruckUpdatable(truckId);
        truck.updateMenuInfo(memberId, updateTruckMenuDto.getTypes());
        truckRepository.save(truck);

        List<TruckCategory> truckCategoryList = truckCategoryRepository.findAllByTruck(truck);
        truckCategoryList.forEach(truckCategoryRepository::delete);
        updateTruckMenuDto.getTruckCategoryCodeSet().forEach(truckCategoryCode -> {
            Category category = categoryRepository.findByCode(truckCategoryCode);
            if (category == null) {
                throw new CustomException(TruckErrorCode.CATEGORY_NOT_FOUND);
            }
            TruckCategory truckCategory = new TruckCategory(truck, category);
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

        List<Integer> createdMenuPrices = updateTruckMenuDto.getTruckMenuDtoList().stream()
                .map(RequestTruck.TruckMenuDto::getPrice)
                .toList();
        truck.updateAvgMenuPrice(createdMenuPrices);
    }

    @Transactional
    public void updateTruckPayment(String truckId, RequestTruck.UpdateTruckPaymentDto updateTruckPaymentDto) {
        String memberId = getMemberId();
        Truck truck = getTruck(truckId);
        validateTruckAccess(truck.getId(), memberId);
        validateTruckUpdatable(truckId);
        truck.updatePaymentInfo(memberId, updateTruckPaymentDto.getPaymentMethods(), updateTruckPaymentDto.getProofIssuanceTypes());
        truckRepository.save(truck);
    }

    @Transactional
    public void setTruckDocuments(String truckId, List<RequestTruck.TruckDocumentDto> documentDtoList) {
        String memberId = getMemberId();
        Truck truck = getTruck(truckId);
        validateTruckAccess(truck.getId(), memberId);

        for (RequestTruck.TruckDocumentDto dto : documentDtoList) {
            DocumentType type = dto.getType();

            if (type == DocumentType.BUSINESS_REGISTRATION) {
                if (dto.getCreateBusinessRegistrationDto() == null) {
                    throw new CustomException(TruckErrorCode.BUSINESS_INFO_MISSED);
                }

                TruckDocument existingDoc = truckDocumentRepository.findByTruckIdAndType(truck.getId(), dto.getType());
                if (existingDoc == null) {
                    BusinessRegistration br = dto.getCreateBusinessRegistrationDto().toEntity(memberId);
                    br = businessRegistrationRepository.save(br);
                    TruckDocument newDoc = dto.toEntity(memberId, br.getId(), truck);
                    truckDocumentRepository.save(newDoc);
                    if (dto.getFileIdList() != null && !dto.getFileIdList().isEmpty()) {
                        saveTruckDocumentPhotos(dto.getFileIdList(), newDoc, memberId);
                    }
                } else {
                    BusinessRegistration br = truckDocumentRepository.getBusinessRegistrationDocumentByTruckId(truck.getId());
                    br.update(memberId,
                            dto.getCreateBusinessRegistrationDto().getBusinessNumber(),
                            dto.getCreateBusinessRegistrationDto().getBusinessName(),
                            dto.getCreateBusinessRegistrationDto().getRepresentativeName(),
                            dto.getCreateBusinessRegistrationDto().getOpeningDate());
                    businessRegistrationRepository.save(br);
                    List<TruckDocumentPhoto> existingPhotos = truckDocumentPhotoRepository.findByTruckDocumentId(existingDoc.getId());
                    existingPhotos.forEach(truckDocumentPhotoRepository::delete);
                    if (dto.getFileIdList() != null && !dto.getFileIdList().isEmpty()) {
                        saveTruckDocumentPhotos(dto.getFileIdList(), existingDoc, memberId);
                    }
                    existingDoc.resubmit(memberId);
                    existingDoc.update(LocalDateTime.now(), memberId);
                    truckDocumentRepository.save(existingDoc);
                }
            } else if (dto.getFileIdList() != null && !dto.getFileIdList().isEmpty()) {
                TruckDocument existingDoc = truckDocumentRepository.findByTruckIdAndType(truck.getId(), dto.getType());

                if (existingDoc == null) {
                    TruckDocument newDoc = dto.toEntity(memberId, truck);
                    truckDocumentRepository.save(newDoc);
                    saveTruckDocumentPhotos(dto.getFileIdList(), newDoc, memberId);
                } else {
                    List<TruckDocumentPhoto> existingPhotos = truckDocumentPhotoRepository.findByTruckDocumentId(existingDoc.getId());
                    existingPhotos.forEach(truckDocumentPhotoRepository::delete);
                    saveTruckDocumentPhotos(dto.getFileIdList(), existingDoc, memberId);
                    existingDoc.update(LocalDateTime.now(), memberId);
                    truckDocumentRepository.save(existingDoc);
                }
            } else {
                throw new CustomException(TruckErrorCode.DOCUMENT_PHOTO_MISSED);
            }
        }
    }

    private void saveTruckDocumentPhotos(List<String> fileIds, TruckDocument doc, String memberId) {
        for (String fileId : fileIds) {
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_DOCUMENT_PHOTO_NOT_FOUND));
            TruckDocumentPhoto photo = TruckDocumentPhoto.builder()
                    .file(file)
                    .updatedBy(memberId)
                    .truckDocument(doc)
                    .build();
            truckDocumentPhotoRepository.save(photo);
        }
    }

    @Transactional
    public void changeOwner(String truckManagerId, String truckId) {
        String memberId = getMemberId();

        TruckManager truckOwner = truckManagerRepository.findByTruckIdAndMemberId(truckId, memberId);
        TruckManager truckMember = truckManagerRepository.findById(truckManagerId).orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_MANAGER_NOT_FOUND));

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
        TruckManager truckMember = truckManagerRepository.findById(managerId).orElseThrow(() -> new CustomException(TruckErrorCode.TRUCK_MANAGER_NOT_FOUND));
        if (truckOwner != null && truckMember != null && Objects.equals(truckOwner.getRole(), TruckManagerRole.OWNER)) {
            truckManagerRepository.delete(truckMember);
        } else throw new CustomException(TruckErrorCode.TRUCK_OWNER_NOT_FOUND);
        Truck truck = truckMember.getTruck();

        NotificationEvent.raise(new ManagerRemovedNotificationEvent(truck.getName(), memberId, truck.getId()));
    }

    @Transactional
    public void deleteTruck(String truckId, boolean isByMemberDelete) {
        TruckManager truckManager = truckManagerRepository.findByTruckIdAndMemberId(truckId, getMemberId());
        if (truckManager != null && Objects.equals(truckManager.getRole(), TruckManagerRole.OWNER)) {
            Truck truck = getTruck(truckId);
            List<EventTruck> eventTruckList = eventTruckRepository.findAllByTruck(truck);
            if (eventTruckList != null) {
                eventTruckList.forEach(eventTruck -> {
                    Event event = eventTruck.getEvent();
                    LocalDate maxDate = EventDateCalculator.getMaxDate(event);
                    LocalDate now = LocalDate.now();
                    // 진행중인 행사에 참여하고 있는 경우 탈퇴 불가 에러
                    if (eventTruck.getStatus().equals(EventTruckStatus.CONFIRMED) && (maxDate.isAfter(now) || maxDate.equals(now))) {
                        throw new CustomException(
                                TruckErrorCode.TRUCK_ALREADY_IN_PROGRESS_EVENT,
                                null,
                                WithdrawalBlockHeaders.byTruckAndEvent(
                                        truck.getId(),
                                        truck.getName(),
                                        event.getId(),
                                        event.getName(),
                                        maxDate
                                )
                        );
                    }
                });
                eventTruckList.forEach(eventTruck -> {
                    Event event = eventTruck.getEvent();
                    // 답변대기중인 행사가 있는 경우 참여 불가 처리
                    if (eventTruck.getStatus().equals(EventTruckStatus.PENDING)) {
                        EventRecruitDetail eventRecruitDetail = eventTruck.getEvent().getRecruitDetail();
                        eventRecruitDetail.decreaseSelectedCount();
                        eventTruck.reject();
                        NotificationEvent.raise(new SelectionCanceledNotificationEvent(event.getId(), event.getName(), eventTruck.getTruck().getName()));
                    }
                });
            }
            List<EventApplication> eventApplicationList = eventApplicationRepository.findAllByTruckId(truckId);
            if (eventApplicationList != null) {
                eventApplicationList.forEach(eventApplication -> {
                    if (eventApplication.getStatus().equals(EventApplicationStatus.PENDING)) {
                        eventService.cancelEventApplication(eventApplication.getId());
                    }
                });
            }
            List<TruckLike> truckLikeList = truckLikeRepository.findAllByTruckId(truckId);
            if (truckLikeList != null) {
                truckLikeList.forEach(truckLikeRepository::delete);
            }
            if (isByMemberDelete) {
                truck.deleteByMember();
            } else {
                truck.delete();
            }
        } else throw new CustomException(TruckErrorCode.TRUCK_OWNER_NOT_FOUND);
    }

    @Transactional(readOnly = true)
    public Page<TruckManagerSummary> getTruckManagerList(String truckId, Pageable pageable) {
        validateNotDeletedTruck(truckId);
        validateTruckAccess(truckId, getMemberId());
        Page<TruckManagerSummary> truckManagerSummaries = truckManagerRepository.findTruckManagerPages(truckId, getMemberId(), pageable);
        return truckManagerSummaries.map(truckManagerSummary -> truckManagerSummary.convertToPreSignedUrl(imageManager));
    }

    @Transactional(readOnly = true)
    public ResponseTruck.GetBusinessRegistrationInfo getTruckBusinessRegistrationDocumentInfo(String truckId) {
        validateTruckAccess(truckId, getMemberId());
        Truck truck = getTruck(truckId);
        BusinessRegistration businessRegistration = truckDocumentRepository.getBusinessRegistrationDocumentByTruckId(truck.getId());
        return ResponseTruck.GetBusinessRegistrationInfo.of(businessRegistration);
    }

    @Transactional(readOnly = true)
    public List<ResponseTruck.GetTruckDocumentFile> getTruckDocumentFiles(String truckId) {
        validateTruckAccess(truckId, getMemberId());
        Truck truck = getTruck(truckId);
        List<TruckDocument> truckDocuments = truckDocumentRepository.getTruckDocumentFiles(truck.getId());
        return truckDocuments.stream()
                .map(truckDocument -> ResponseTruck.GetTruckDocumentFile.of(truckDocument, imageManager))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ResponseTruck.GetBackOfficeTruckDocumentDto> getBackOfficeTruckDocuments(String nickname, String phone, TruckDocumentStatus status,
                                                                                         LocalDate requestedStartAt, LocalDate requestedEndAt, LocalDate processedStartAt, LocalDate processedEndAt, Pageable pageable) {
        Page<BackOfficeTruckDocument> truckDocumentPage = truckDocumentRepository.getBackOfficeTruckDocuments(DocumentType.BUSINESS_REGISTRATION, nickname, phone, status,
                requestedStartAt, requestedEndAt, processedStartAt, processedEndAt, pageable);
        List<String> documentIds = truckDocumentPage.getContent()
                .stream()
                .map(it -> it.getTruckDocument().getId())
                .toList();

        Map<String, List<String>> photoMap =
                truckDocumentRepository.findPhotosByTruckDocumentIds(documentIds);

        return truckDocumentPage.map(it ->
                ResponseTruck.GetBackOfficeTruckDocumentDto.of(
                        it,
                        photoMap.getOrDefault(it.getTruckDocument().getId(), List.of())
                )
        );
    }

    //todo 관리자 계정 권한 검증 추가
    @Transactional
    public void approveTruckDocument(String truckId, DocumentType documentType) {
        TruckDocument truckDocument = getTruckDocument(truckId, documentType);
        truckDocument.approve(getMemberId());
        truckDocumentRepository.save(truckDocument);
        if (documentType.equals(DocumentType.BUSINESS_REGISTRATION)) {
            NotificationEvent.raise(new BusinessRegistrationApprovedNotificationEvent(truckDocument.getTruck().getId(), truckDocument.getTruck().getName()));
        }
    }

    @Transactional
    public void rejectTruckDocument(String truckId, DocumentType documentType, String rejectionReason) {
        TruckDocument truckDocument = getTruckDocument(truckId, documentType);
        truckDocument.reject(getMemberId(), rejectionReason);
        truckDocumentRepository.save(truckDocument);
        if (documentType.equals(DocumentType.BUSINESS_REGISTRATION)) {
            NotificationEvent.raise(new BusinessRegistrationRejectedNotificationEvent(truckDocument.getTruck().getId(), truckDocument.getTruck().getName()));
        }
    }

    @Transactional
    public Truck getTruckById(String id) {
        return this.getTruck(id);
    }


    @Transactional
    public ResponseTruck.GetTruckManagerContactDto getTruckManagerContract(String truckId) {
        String memberId = null;
        try {
            memberId = getMemberId();
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
            if (member.getSocialLoginInfo().getType().equals(SocialLoginType.UNREGISTERED)) {
                throw new CustomException(TruckErrorCode.TRUCK_CONTACT_ACCESS_DENIED);
            }

            String phone = truckManagerRepository.getTruckOwnerPhone(truckId);
            if (phone == null) {
                throw new CustomException(TruckErrorCode.TRUCK_MANAGER_NOT_FOUND);
            }
            truckContactAccessLogService.saveTruckContactAccessLog(
                    TruckContactAccessLog.builder()
                            .truckId(truckId)
                            .memberId(memberId)
                            .accessStatus(AccessStatus.SUCCESS)
                            .build()
            );

            return ResponseTruck.GetTruckManagerContactDto.of(phone);
        } catch (Exception e) {
            truckContactAccessLogService.saveTruckContactAccessLog(
                    TruckContactAccessLog.builder()
                            .truckId(truckId)
                            .memberId(memberId)
                            .accessStatus(AccessStatus.FAIL)
                            .failureReason(e.getMessage())
                            .build()
            );
            throw e;
        }
    }


}

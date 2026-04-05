package com.barowoori.foodpinbackend.event.command.application.service;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.common.exception.WithdrawalBlockHeaders;
import com.barowoori.foodpinbackend.document.command.application.service.emailEvent.EventAppliedTruckDocumentSubmissionEvent;
import com.barowoori.foodpinbackend.event.command.application.dto.RequestEvent;
import com.barowoori.foodpinbackend.event.command.application.dto.ResponseEvent;
import com.barowoori.foodpinbackend.event.command.domain.exception.EventErrorCode;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventDashboardCount;
import com.barowoori.foodpinbackend.event.command.domain.service.EventContactAccessLogService;
import com.barowoori.foodpinbackend.event.command.domain.service.EventDateCalculator;
import com.barowoori.foodpinbackend.event.query.application.EventRegionFullNameGenerator;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.repository.FileRepository;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.EventLike;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.EventLikeRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.ApplicationReceivedNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.InterestRegisteredNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.SelectionCanceledNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.event.SelectionConfirmedNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.truck.EventCastedNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.truck.EventRecruitmentCanceledNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.truck.EventUpdatedNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.truck.TruckSelectionConfirmedNotificationEvent;
import com.barowoori.foodpinbackend.region.command.domain.model.Region;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
import com.barowoori.foodpinbackend.truck.command.domain.exception.TruckErrorCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckManagerRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final FileRepository fileRepository;
    private final EventPhotoRepository eventPhotoRepository;
    private final RegionDoRepository regionDoRepository;
    private final EventRegionRepository eventRegionRepository;
    private final EventRecruitDetailRepository eventRecruitDetailRepository;
    private final EventDateRepository eventDateRepository;
    private final CategoryRepository categoryRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final EventDocumentRepository eventDocumentRepository;
    private final EventViewRepository eventViewRepository;
    private final EventLikeRepository eventLikeRepository;
    private final TruckRepository truckRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final EventApplicationDateRepository eventApplicationDateRepository;
    private final TruckManagerRepository truckManagerRepository;
    private final EventNoticeRepository eventNoticeRepository;
    private final EventProposalRepository eventProposalRepository;
    private final EventTruckRepository eventTruckRepository;
    private final EventNoticeViewRepository eventNoticeViewRepository;
    private final EventRegionFullNameGenerator eventRegionFullNameGenerator;
    private final EventContactAccessLogService eventContactAccessLogService;
    private final MemberRepository memberRepository;

    private String getMemberId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Event getEvent(String eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(EventErrorCode.NOT_FOUND_EVENT));
    }

    private Truck getTruck(String truckId) {
        return truckRepository.findById(truckId)
                .orElseThrow(() -> new CustomException(TruckErrorCode.NOT_FOUND_TRUCK));
    }

    private void validateEventAccess(Event event, String memberId) {
        if (!event.getCreatedBy().equals(memberId)) {
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }
    }

    private void validateEventUpdatable(String eventId) {
        if (Boolean.TRUE.equals(eventApplicationRepository.existsSelectedApplicationByEventId(eventId))) {
            throw new CustomException(EventErrorCode.SELECTED_EVENT_APPLICATION_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEvent.GetEventUpdateAvailabilityDto getEventUpdateAvailability(String eventId) {
        String memberId = getMemberId();
        Event event = getEvent(eventId);

        if (!event.getCreatedBy().equals(memberId)) {
            return ResponseEvent.GetEventUpdateAvailabilityDto.of(Boolean.FALSE);
        }

        boolean hasSelectedEventApplication = Boolean.TRUE.equals(eventApplicationRepository.existsSelectedApplicationByEventId(eventId));
        return ResponseEvent.GetEventUpdateAvailabilityDto.of(!hasSelectedEventApplication);
    }

    @Transactional
    public void createEvent(RequestEvent.CreateEventDto createEventDto) {
        String memberId = getMemberId();
        Event event = createEventDto.getEventInfoDto().toEntity(memberId);
        createEvent(
                event,
                createEventDto.getEventInfoDto().getFileIdList(),
                createEventDto.getEventInfoDto().getRegionCode(),
                createEventDto.getEventInfoDto().getEventDateDtoList(),
                createEventDto.getEventRecruitDto(),
                createEventDto.getEventTargetDto(),
                createEventDto.getEventDetailDto(),
                memberId
        );
    }

    @Transactional
    public void createBackOfficeEvent(RequestEvent.CreateBackOfficeEventDto createBackOfficeEventDto) {
        String memberId = getMemberId();
        Event event = createBackOfficeEventDto.getEventInfoDto().toEntity(memberId);
        createEvent(
                event,
                createBackOfficeEventDto.getEventInfoDto().getFileIdList(),
                createBackOfficeEventDto.getEventInfoDto().getRegionCode(),
                createBackOfficeEventDto.getEventInfoDto().getEventDateDtoList(),
                createBackOfficeEventDto.getEventRecruitDto(),
                createBackOfficeEventDto.getEventTargetDto(),
                createBackOfficeEventDto.getEventDetailDto(),
                memberId
        );
    }

    private void createEvent(Event event,
                             List<String> fileIdList,
                             String regionCode,
                             List<RequestEvent.EventDateDto> eventDateDtoList,
                             RequestEvent.EventRecruitDto eventRecruitDto,
                             RequestEvent.EventTargetDto eventTargetDto,
                             RequestEvent.EventDetailDto eventDetailDto,
                             String memberId) {
        event.updateDetailInfo(
                eventDetailDto.getDescription(),
                eventDetailDto.getGuidelines(),
                eventDetailDto.getContact()
        );
        event.updateTargetInfo(
                eventTargetDto.getTruckTypes(),
                eventTargetDto.getSaleType(),
                eventTargetDto.getPriceRange(),
                eventTargetDto.getCateringDetail()
        );
        eventRepository.save(event);

        if (!Objects.equals(fileIdList, null) && !fileIdList.isEmpty()) {
            for (String fileId : fileIdList) {
                File file = fileRepository.findById(fileId)
                        .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_PHOTO_NOT_FOUND));
                EventPhoto eventPhoto = EventPhoto.builder().file(file).updatedBy(memberId).event(event).build();
                eventPhotoRepository.save(eventPhoto);
            }
        }

        EventView eventView = EventView.builder().event(event).views(0).build();
        eventViewRepository.save(eventView);
        event.initEventView(eventView);

        RegionInfo regionInfo = regionDoRepository.findByCode(regionCode);
        if (regionInfo == null) {
            throw new CustomException(EventErrorCode.EVENT_REGION_NOT_FOUND);
        }
        EventRegion eventRegion = EventRegion.builder().regionType(regionInfo.getRegionType()).regionId(regionInfo.getRegionId()).event(event).build();
        eventRegion = eventRegionRepository.save(eventRegion);
        event.initEventRegion(eventRegion);

        if (Objects.equals(eventRecruitDto.getRecruitEndDateTime(), null)) {
            LocalDateTime lastEndDateTime = eventDateDtoList.stream()
                    .map(dto -> LocalDateTime.of(dto.getDate(), dto.getEndTime()))
                    .max(LocalDateTime::compareTo)
                    .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_DATE_NOT_FOUND));

            eventRecruitDto.setRecruitEndDateTime(lastEndDateTime.toLocalDate().atTime(23, 59, 59));
        }
        EventRecruitDetail eventRecruitDetail = eventRecruitDto.toEntity(
                event,
                eventDetailDto.getGeneratorRequirement(),
                eventDetailDto.getElectricitySupportAvailability()
        );
        eventRecruitDetailRepository.save(eventRecruitDetail);
        event.initEventRecruitDetail(eventRecruitDetail);

        eventDateDtoList.forEach(eventDateDto -> {
            EventDate eventDate = eventDateDto.toEntity(event);
            eventDateRepository.save(eventDate);
        });
        List<Category> categories = new ArrayList<>();
        eventTargetDto.getEventCategoryCodeList().forEach(eventCategoryCode -> {
            Category category = categoryRepository.findByCode(eventCategoryCode);
            if (category == null) {
                throw new CustomException(EventErrorCode.EVENT_CATEGORY_NOT_FOUND);
            }
            EventCategory eventCategory = EventCategory.builder().event(event).category(category).build();
            eventCategoryRepository.save(eventCategory);
            categories.add(category);
        });

        Event registeredEvent = eventRepository.save(event);
        List<RegionCode> regionNames = eventRegionFullNameGenerator.findRegionCodesByEventId(registeredEvent.getId());
        String regionList = eventRegionFullNameGenerator.makeRegionList(regionNames);

        Region region = regionDoRepository.findRegionByCode(regionCode);
        Map<RegionType, String> regionIds = regionDoRepository.extractParentRegions(region);
        NotificationEvent.raise(new InterestRegisteredNotificationEvent(registeredEvent.getId(), registeredEvent.getName(), regionList, memberId, regionIds, categories));
    }

    @Transactional
    public void updateEventInfo(String eventId, RequestEvent.UpdateEventInfoDto updateEventInfoDto) {
        String memberId = getMemberId();
        Event event = getEvent(eventId);
        validateEventAccess(event, memberId);
        validateEventUpdatable(eventId);
        event.updateBasicInfo(
                updateEventInfoDto.getName(),
                updateEventInfoDto.getType(),
                updateEventInfoDto.getExpectedParticipants()
        );

        List<EventPhoto> photoList = eventPhotoRepository.findAllByEvent(event);
        photoList.forEach(eventPhotoRepository::delete);
        eventPhotoRepository.flush();

        if (!Objects.equals(updateEventInfoDto.getFileIdList(), null) && !updateEventInfoDto.getFileIdList().isEmpty()) {
            for (String fileId : updateEventInfoDto.getFileIdList()) {
                File file = fileRepository.findById(fileId)
                        .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_PHOTO_NOT_FOUND));
                EventPhoto eventPhoto = EventPhoto.builder().file(file).updatedBy(memberId).event(event).build();
                eventPhotoRepository.save(eventPhoto);
            }
        }

        List<EventDate> eventDateList = eventDateRepository.findAllByEvent(event);
        eventDateList.forEach(eventDateRepository::delete);
        updateEventInfoDto.getEventDateDtoList().forEach(eventDateDto -> {
            EventDate eventDate = eventDateDto.toEntity(event);
            eventDateRepository.save(eventDate);
        });

        event.initEventRegion(null);
        EventRegion eventRegion = eventRegionRepository.findByEvent(event);
        RegionInfo regionInfo = regionDoRepository.findByCode(updateEventInfoDto.getRegionCode());
        if (regionInfo == null)
            throw new CustomException(EventErrorCode.EVENT_REGION_NOT_FOUND);
        eventRegion.updateRegion(regionInfo.getRegionType(), regionInfo.getRegionId());
        eventRegionRepository.save(eventRegion);
        event.initEventRegion(eventRegion);

        eventRepository.save(event);
        NotificationEvent.raise(new EventUpdatedNotificationEvent(event.getId(), event.getName()));
    }

    @Transactional
    public void updateEventRecruit(String eventId, RequestEvent.UpdateEventRecruitDto eventRecruitDto) {
        Event event = getEvent(eventId);
        validateEventAccess(event, getMemberId());
        validateEventUpdatable(eventId);
        EventRecruitDetail eventRecruitDetail = eventRecruitDetailRepository.findByEvent(event);
        if (eventRecruitDetail == null)
            throw new CustomException(EventErrorCode.EVENT_RECRUIT_DETAIL_NOT_FOUND);
        if (Objects.equals(eventRecruitDto.getRecruitEndDateTime(), null)) {
            LocalDateTime lastEndDateTime = event.getEventDates().stream()
                    .map(eventDate -> LocalDateTime.of(eventDate.getDate(), eventDate.getEndTime()))
                    .max(LocalDateTime::compareTo)
                    .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_DATE_NOT_FOUND));

            eventRecruitDto.setRecruitEndDateTime(lastEndDateTime.toLocalDate().atTime(23, 59, 59));
        }
        eventRecruitDetail.update(
                eventRecruitDto.getRecruitEndDateTime(),
                eventRecruitDto.getRecruitCount(),
                eventRecruitDto.getIsFullAttendanceRequired(),
                eventRecruitDetail.getGeneratorRequirement(),
                eventRecruitDetail.getElectricitySupportAvailability(),
                eventRecruitDetail.getEntryFee(),
                eventRecruitDto.getIsRecruitEndOnSelection()
        );
        eventRecruitDetailRepository.save(eventRecruitDetail);
        eventRepository.save(event);
        NotificationEvent.raise(new EventUpdatedNotificationEvent(event.getId(), event.getName()));
    }

    @Transactional
    public void updateEventDetail(String eventId, RequestEvent.UpdateEventDetailDto updateEventDetailDto) {
        Event event = getEvent(eventId);
        validateEventAccess(event, getMemberId());
        validateEventUpdatable(eventId);
        EventRecruitDetail eventRecruitDetail = eventRecruitDetailRepository.findByEvent(event);
        if (eventRecruitDetail == null) {
            throw new CustomException(EventErrorCode.EVENT_RECRUIT_DETAIL_NOT_FOUND);
        }

        eventRecruitDetail.update(
                eventRecruitDetail.getRecruitEndDateTime(),
                eventRecruitDetail.getRecruitCount(),
                eventRecruitDetail.getIsFullAttendanceRequired(),
                updateEventDetailDto.getGeneratorRequirement(),
                updateEventDetailDto.getElectricitySupportAvailability(),
                eventRecruitDetail.getEntryFee(),
                eventRecruitDetail.getIsRecruitEndOnSelection()
        );

        event.updateDetailInfo(
                updateEventDetailDto.getDescription(),
                updateEventDetailDto.getGuidelines(),
                updateEventDetailDto.getContact()
        );
        eventRecruitDetailRepository.save(eventRecruitDetail);
        eventRepository.save(event);
        NotificationEvent.raise(new EventUpdatedNotificationEvent(event.getId(), event.getName()));
    }

    @Transactional
    public void updateEventTarget(String eventId, RequestEvent.UpdateEventTargetDto updateEventTargetDto) {
        Event event = getEvent(eventId);
        validateEventAccess(event, getMemberId());
        validateEventUpdatable(eventId);

        List<EventCategory> eventCategoryList = eventCategoryRepository.findAllByEvent(event);
        eventCategoryList.forEach(eventCategoryRepository::delete);
        updateEventTargetDto.getEventCategoryCodeList().forEach(eventCategoryCode -> {
            Category category = categoryRepository.findByCode(eventCategoryCode);
            if (category == null)
                throw new CustomException(EventErrorCode.EVENT_CATEGORY_NOT_FOUND);
            EventCategory eventCategory = EventCategory.builder().event(event).category(category).build();
            eventCategoryRepository.save(eventCategory);
        });

        event.updateTargetInfo(
                updateEventTargetDto.getTruckTypes(),
                updateEventTargetDto.getSaleType(),
                updateEventTargetDto.getPriceRange(),
                updateEventTargetDto.getCateringDetail()
        );

        eventRepository.save(event);
        NotificationEvent.raise(new EventUpdatedNotificationEvent(event.getId(), event.getName()));
    }

    @Transactional
    public void updateEventDocument(String eventId, RequestEvent.UpdateEventDocumentDto updateEventDocumentDto) {
        Event event = getEvent(eventId);
        validateEventAccess(event, getMemberId());
        validateEventUpdatable(eventId);
        List<EventDocument> eventDocumentList = eventDocumentRepository.findByEventId(eventId);
        if (eventDocumentList != null) {
            eventDocumentList.forEach(eventDocumentRepository::delete);
        }
        if (updateEventDocumentDto.getEventDocumentTypeList() != null) {
            updateEventDocumentDto.getEventDocumentTypeList().forEach(documentType -> {
                EventDocument eventDocument = EventDocument.builder().event(event).type(documentType).build();
                eventDocumentRepository.save(eventDocument);
            });
        }
        event.updateDocumentInfo(
                updateEventDocumentDto.getSubmissionEmail(),
                updateEventDocumentDto.getDocumentSubmissionTarget()
        );
        eventRepository.save(event);
        NotificationEvent.raise(new EventUpdatedNotificationEvent(event.getId(), event.getName()));
    }

    @Transactional
    public void updateBackOfficeRecruitmentUrl(String eventId, RequestEvent.UpdateBackOfficeRecruitmentUrlDto updateBackOfficeRecruitmentUrlDto) {
        Event event = getEvent(eventId);
        event.updateRecruitmentUrl(updateBackOfficeRecruitmentUrlDto.getRecruitmentUrl());
        eventRepository.save(event);
    }

    @Transactional
    public void updateBackOfficeEventHidden(String eventId, RequestEvent.UpdateBackOfficeEventHiddenDto updateBackOfficeEventHiddenDto) {
        Event event = getEvent(eventId);
        event.updateHidden(updateBackOfficeEventHiddenDto.getIsHidden());
        eventRepository.save(event);
    }

    @Transactional
    public void addRecruitmentUrlClickCount(String eventId) {
        Event event = getEvent(eventId);
        event.addRecruitmentUrlClickCount();
        eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(String eventId) {
        Event event = getEvent(eventId);
        validateEventAccess(event, getMemberId());

        EventRecruitDetail eventRecruitDetail = eventRecruitDetailRepository.findByEvent(event);
        LocalDate maxDate = EventDateCalculator.getMaxDate(event);
        LocalDate now = LocalDate.now();

        boolean hasConfirmedEventTruck = event.getEventTrucks().stream().anyMatch(eventTruck -> eventTruck.getStatus() == EventTruckStatus.CONFIRMED);
        boolean isWithinEventPeriod = maxDate.isAfter(now) || maxDate.isEqual(now);
        if (hasConfirmedEventTruck && isWithinEventPeriod) {
            throw new CustomException(
                    EventErrorCode.CONFIRMED_EVENT_TRUCK_EXISTS,
                    null,
                    WithdrawalBlockHeaders.byHostedEvent(event.getId(), event.getName(), maxDate)
            );
        }

        if (eventRecruitDetail != null && eventRecruitDetail.getRecruitingStatus().equals(EventRecruitingStatus.RECRUITING)) {
            eventRecruitDetail.updateStatus(EventRecruitingStatus.RECRUITMENT_CANCELLED);
            eventRecruitDetail.closeSelection();
            NotificationEvent.raise(new EventRecruitmentCanceledNotificationEvent(event.getId(), event.getName()));
        }

        List<EventLike> eventLikeList = eventLikeRepository.findAllByEventId(eventId);
        if (eventLikeList != null) {
            eventLikeList.forEach(eventLikeRepository::delete);
        }
        event.delete();
    }

    @Transactional
    public void proposeEvent(RequestEvent.ProposeEventDto proposeEventDto) {
        Event event = getEvent(proposeEventDto.getEventId());
        validateEventAccess(event, getMemberId());

        EventProposal eventProposal = eventProposalRepository.findByEventIdAndTruckId(proposeEventDto.getEventId(), proposeEventDto.getTruckId());
        if (eventProposal != null) {
            throw new CustomException(EventErrorCode.ALREADY_PROPOSED_TRUCK);
        }
        EventProposal newEventProposal = proposeEventDto.toEntity(event, getTruck(proposeEventDto.getTruckId()));
        eventProposalRepository.save(newEventProposal);
        NotificationEvent.raise(new EventCastedNotificationEvent(event.getId(), proposeEventDto.getTruckId(), event.getName()));
    }

    @Transactional
    public void applyEvent(RequestEvent.ApplyEventDto applyEventDto) {
        TruckManager truckManager = truckManagerRepository.findByTruckIdAndMemberId(applyEventDto.getTruckId(), getMemberId());
        if (truckManager == null)
            throw new CustomException(TruckErrorCode.TRUCK_MANAGER_NOT_FOUND);
        EventApplication originalEventApplication = eventApplicationRepository.findByTruckIdAndEventId(applyEventDto.getTruckId(), applyEventDto.getEventId());
        if (originalEventApplication != null)
            throw new CustomException(EventErrorCode.ALREADY_APPLIED_EVENT);
        Event event = getEvent(applyEventDto.getEventId());
        Truck truck = getTruck(applyEventDto.getTruckId());

        List<String> eventDateIdList;
        if (Boolean.TRUE.equals(event.getRecruitDetail().getIsFullAttendanceRequired())) {
            eventDateIdList = event.getEventDates().stream()
                    .map(EventDate::getId)
                    .toList();
        } else {
            eventDateIdList = applyEventDto.getEventDateIdList();
        }

        EventApplication eventApplication = applyEventDto.toEntity(truck, event);
        eventApplication = eventApplicationRepository.save(eventApplication);

        for (String eventDateId : eventDateIdList) {
            EventDate eventDate = eventDateRepository.findById(eventDateId)
                    .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_DATE_NOT_FOUND));

            EventApplicationDate eventApplicationDate = EventApplicationDate.builder().eventDate(eventDate).eventApplication(eventApplication).build();
            eventApplicationDateRepository.save(eventApplicationDate);
        }
        //지원자수 증가
        eventRecruitDetailRepository.incrementApplicantCount(event.getRecruitDetail().getId());

        NotificationEvent.raise(new ApplicationReceivedNotificationEvent(event.getId(), event.getName(), eventApplication.getId()));
        NotificationEvent.raise(new EventAppliedTruckDocumentSubmissionEvent(event, truck));
    }

    @Transactional
    public void cancelEventApplication(String eventApplicationId) {
        EventApplication application = eventApplicationRepository.findById(eventApplicationId)
                .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_APPLICATION_NOT_FOUND));

        String memberId = getMemberId();
        Truck truck = application.getTruck();

        TruckManager truckManager = truckManagerRepository.findByTruckIdAndMemberId(truck.getId(), memberId);
        if (truckManager == null) {
            throw new CustomException(TruckErrorCode.TRUCK_MANAGER_NOT_FOUND);
        }

        if (application.getStatus() == EventApplicationStatus.SELECTED) {
            throw new CustomException(EventErrorCode.ALREADY_SELECTED_EVENT_APPLICATION);
        }
        EventRecruitDetail eventRecruitDetail = application.getEvent().getRecruitDetail();
        eventRecruitDetail.decreaseApplicantCount();

        eventApplicationDateRepository.deleteAll(application.getDates());
        eventApplicationRepository.delete(application);
    }


    @Transactional
    public void handleEventTruck(RequestEvent.HandleEventTruckDto handleEventTruckDto) {
        EventTruck eventTruck = eventTruckRepository.findById(handleEventTruckDto.getEventTruckId())
                .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_TRUCK_NOT_FOUND));
        Event event = eventTruck.getEvent();
        TruckManager truckManager = truckManagerRepository.findByTruckIdAndMemberId(eventTruck.getTruck().getId(), getMemberId());
        if (truckManager == null) {
            throw new CustomException(TruckErrorCode.TRUCK_MANAGER_NOT_FOUND);
        }

        if (!eventTruck.getStatus().equals(EventTruckStatus.PENDING)) {
            throw new CustomException(EventErrorCode.ALREADY_HANDLED_EVENT_TRUCK);
        }

        if (handleEventTruckDto.getEventTruckStatus().equals(EventTruckStatus.CONFIRMED)) {
            eventTruck.confirm();
            NotificationEvent.raise(new SelectionConfirmedNotificationEvent(event.getId(), event.getName(), eventTruck.getTruck().getName(), eventTruck.getId()));
            NotificationEvent.raise(new TruckSelectionConfirmedNotificationEvent(event.getId(), event.getName(), eventTruck.getId(), eventTruck.getTruck().getId()));
        } else if (handleEventTruckDto.getEventTruckStatus().equals(EventTruckStatus.REJECTED)) {
            EventRecruitDetail eventRecruitDetail = eventTruck.getEvent().getRecruitDetail();
            eventRecruitDetail.decreaseSelectedCount();
            eventTruck.reject();
            NotificationEvent.raise(new SelectionCanceledNotificationEvent(event.getId(), event.getName(), eventTruck.getTruck().getName()));
        } else throw new CustomException(EventErrorCode.WRONG_EVENT_TRUCK_STATUS);

        eventTruckRepository.save(eventTruck);
    }

    @Transactional(readOnly = true)
    public Page<ResponseEvent.GetEventNoticeDto> getEventNotices(String eventId, Pageable pageable) {
        return eventNoticeRepository.findEventNoticeListByEventId(eventId, pageable)
                .map(ResponseEvent.GetEventNoticeDto::of);
    }

    @Transactional
    public ResponseEvent.GetEventNoticeDetailForCreatorDto getEventNoticeDetailForCreator(String noticeId) {
        EventNotice eventNotice = eventNoticeRepository.findEventNoticeForCreator(noticeId);
        if (eventNotice == null) {
            throw new CustomException(EventErrorCode.EVENT_NOTICE_NOT_FOUND);
        }
        validateEventAccess(eventNotice.getEvent(), getMemberId());

        return ResponseEvent.GetEventNoticeDetailForCreatorDto.of(eventNotice);
    }

    @Transactional
    public ResponseEvent.GetEventNoticeDetailForTruckDto getEventNoticeDetailForTruck(String truckId, String noticeId) {
        EventNotice eventNotice = eventNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_NOTICE_NOT_FOUND));

        EventTruck eventTruck = eventTruckRepository.findConfirmedEventTruck(eventNotice.getEvent().getId(), truckId);
        if (eventTruck == null) {
            throw new CustomException(EventErrorCode.EVENT_TRUCK_NOT_FOUND);
        }

        EventNoticeView eventNoticeView = eventNoticeViewRepository.findByEventNoticeAndEventTruck(eventNotice, eventTruck);
        if (eventNoticeView == null) {
            eventNoticeView = EventNoticeView.builder()
                    .eventTruck(eventTruck)
                    .eventNotice(eventNotice)
                    .build();
            eventNoticeViewRepository.save(eventNoticeView);
        }
        return ResponseEvent.GetEventNoticeDetailForTruckDto.of(eventNotice);

    }

    @Transactional
    public Event getEventById(String id) {
        return this.getEvent(id);
    }

    @Transactional(readOnly = true)
    public ResponseEvent.GetTruckAppliedEventDashboard getTruckAppliedEventDashboard(String truckId) {

        Integer appliedCount = eventApplicationRepository.findTruckAppliedRecruitingApplications(truckId).intValue()
                + eventTruckRepository.findPendingEventsByTruckId(truckId).intValue();

        Integer progressCount = eventTruckRepository.findProgressEventsByTruckId(truckId).intValue();

        Integer endCount = eventTruckRepository.findCompletedEventsByTruckId(truckId).intValue();

        return ResponseEvent.GetTruckAppliedEventDashboard.of(
                appliedCount,
                progressCount,
                endCount
        );
    }


    @Transactional(readOnly = true)
    public ResponseEvent.GetEventDashboard getEventDashboard() {
        String memberId = getMemberId();
        EventDashboardCount dashboardCount = eventRepository.findEventDashboardCount(memberId);

        return ResponseEvent.GetEventDashboard.of(
                dashboardCount.getRecruitingCount().intValue(),
                dashboardCount.getProgressCount().intValue(),
                dashboardCount.getEndCount().intValue()
        );
    }

    @Transactional
    public ResponseEvent.GetEventContactDto getEventContact(String eventId){
        String memberId = null;
        try {
            memberId = getMemberId();
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

            if (member.getSocialLoginInfo().getType().equals(SocialLoginType.UNREGISTERED)) {
                throw new CustomException(EventErrorCode.EVENT_CONTACT_ACCESS_DENIED);
            }

            String phone = eventRepository.getEventPhone(eventId);
            eventContactAccessLogService.saveEventContactAccessLog(
                    EventContactAccessLog.builder()
                            .eventId(eventId)
                            .memberId(memberId)
                            .accessStatus(AccessStatus.SUCCESS)
                            .build()
            );

            return ResponseEvent.GetEventContactDto.of(phone);
        } catch (Exception e) {
            eventContactAccessLogService.saveEventContactAccessLog(
                    EventContactAccessLog.builder()
                            .eventId(eventId)
                            .memberId(memberId)
                            .accessStatus(AccessStatus.FAIL)
                            .failureReason(e.getMessage())
                            .build()
            );
            throw e;
        }
    }
}

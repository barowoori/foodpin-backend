package com.barowoori.foodpinbackend.event.command.application.service;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.event.command.application.dto.RequestEvent;
import com.barowoori.foodpinbackend.event.command.application.dto.ResponseEvent;
import com.barowoori.foodpinbackend.event.command.domain.exception.EventErrorCode;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.*;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.repository.FileRepository;
import com.barowoori.foodpinbackend.member.command.domain.model.EventLike;
import com.barowoori.foodpinbackend.member.command.domain.repository.EventLikeRepository;
import com.barowoori.foodpinbackend.notification.command.domain.model.ApplicationReceivedNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.SelectionCanceledNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.SelectionConfirmedNotificationEvent;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
import com.barowoori.foodpinbackend.truck.command.domain.exception.TruckErrorCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManager;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckManagerRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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

    @Transactional
    public void createEvent(RequestEvent.CreateEventDto createEventDto) {
        String memberId = getMemberId();

        Event event = createEventDto.getEventInfoDto().toEntity(memberId);
        eventRepository.save(event);

        if (!Objects.equals(createEventDto.getEventInfoDto().getFileIdList(), null) && !createEventDto.getEventInfoDto().getFileIdList().isEmpty()) {
            for (String fileId : createEventDto.getEventInfoDto().getFileIdList()) {
                File file = fileRepository.findById(fileId)
                        .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_PHOTO_NOT_FOUND));
                EventPhoto eventPhoto = EventPhoto.builder().file(file).updatedBy(memberId).event(event).build();
                eventPhotoRepository.save(eventPhoto);
            }
        }

        EventView eventView = EventView.builder().event(event).views(0).build();
        eventViewRepository.save(eventView);
        event.initEventView(eventView);

        RegionInfo regionInfo = regionDoRepository.findByCode(createEventDto.getRegionCode());
        if (regionInfo == null)
            throw new CustomException(EventErrorCode.EVENT_REGION_NOT_FOUND);
        EventRegion eventRegion = EventRegion.builder().regionType(regionInfo.getRegionType()).regionId(regionInfo.getRegionId()).event(event).build();
        eventRegionRepository.save(eventRegion);
        event.initEventRegion(eventRegion);

        EventRecruitDetail eventRecruitDetail = createEventDto.getEventRecruitDto().toEntity(event);
        eventRecruitDetailRepository.save(eventRecruitDetail);
        event.initEventRecruitDetail(eventRecruitDetail);

        createEventDto.getEventDateDtoList().forEach(eventDateDto -> {
            EventDate eventDate = eventDateDto.toEntity(event);
            eventDateRepository.save(eventDate);
        });

        createEventDto.getEventCategoryCodeList().forEach(eventCategoryCode -> {
            Category category = categoryRepository.findByCode(eventCategoryCode);
            if (category == null)
                throw new CustomException(EventErrorCode.EVENT_CATEGORY_NOT_FOUND);
            EventCategory eventCategory = EventCategory.builder().event(event).category(category).build();
            eventCategoryRepository.save(eventCategory);
        });

        if (!Objects.equals(createEventDto.getEventDocumentTypeList(), null) && !createEventDto.getEventDocumentTypeList().isEmpty()) {
            createEventDto.getEventDocumentTypeList().forEach(documentType -> {
                EventDocument eventDocument = EventDocument.builder().event(event).type(documentType).build();
                eventDocumentRepository.save(eventDocument);
            });
        }

        eventRepository.save(event);
    }

    @Transactional
    public void updateEventInfo(String eventId, RequestEvent.UpdateEventInfoDto updateEventInfoDto) {
        String memberId = getMemberId();
        Event event = getEvent(eventId);

        event.updateName(updateEventInfoDto.getName());

        if (!Objects.equals(updateEventInfoDto.getFileIdList(), null) && !updateEventInfoDto.getFileIdList().isEmpty()) {
            List<EventPhoto> photoList = eventPhotoRepository.findAllByEvent(event);
            photoList.forEach(eventPhotoRepository::delete);
            eventPhotoRepository.flush();
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
    }

    @Transactional
    public void updateEventRecruit(String eventId, RequestEvent.EventRecruitDto eventRecruitDto) {
        Event event = getEvent(eventId);

        EventRecruitDetail eventRecruitDetail = eventRecruitDetailRepository.findByEvent(event);
        if (eventRecruitDetail == null)
            throw new CustomException(EventErrorCode.EVENT_RECRUIT_DETAIL_NOT_FOUND);
        eventRecruitDetailRepository.delete(eventRecruitDetail);
        EventRecruitDetail updatedEventRecruitDetail = eventRecruitDto.toEntity(event);
        eventRecruitDetailRepository.save(updatedEventRecruitDetail);
        event.initEventRecruitDetail(updatedEventRecruitDetail);

        eventRepository.save(event);
    }

    @Transactional
    public void updateEventDetail(String eventId, RequestEvent.UpdateEventDetailDto updateEventDetailDto) {
        Event event = getEvent(eventId);

        List<EventCategory> eventCategoryList = eventCategoryRepository.findAllByEvent(event);
        eventCategoryList.forEach(eventCategoryRepository::delete);
        updateEventDetailDto.getEventCategoryCodeList().forEach(eventCategoryCode -> {
            Category category = categoryRepository.findByCode(eventCategoryCode);
            if (category == null)
                throw new CustomException(EventErrorCode.EVENT_CATEGORY_NOT_FOUND);
            EventCategory eventCategory = EventCategory.builder().event(event).category(category).build();
            eventCategoryRepository.save(eventCategory);
        });
        event.updateDescription(updateEventDetailDto.getDescription());
        event.updateGuidelines(updateEventDetailDto.getGuidelines());
        eventRepository.save(event);
    }

    @Transactional
    public void updateEventDocument(String eventId, RequestEvent.UpdateEventDocumentDto updateEventDocumentDto) {
        Event event = getEvent(eventId);

        List<EventDocument> eventDocumentList = eventDocumentRepository.findByEventId(eventId);
        if (eventDocumentList != null)
            eventDocumentList.forEach(eventDocumentRepository::delete);
        updateEventDocumentDto.getEventDocumentTypeList().forEach(documentType -> {
            EventDocument eventDocument = EventDocument.builder().event(event).type(documentType).build();
            eventDocumentRepository.save(eventDocument);
        });
        event.updateSubmissionEmail(updateEventDocumentDto.getSubmissionEmail());
        event.updateDocumentSubmissionTarget(updateEventDocumentDto.getDocumentSubmissionTarget());
        eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(String eventId) {
        String memberId = getMemberId();
        Event event = getEvent(eventId);
        if (!event.isCreator(memberId)) {
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }
        if (event.isCreator(memberId)) {
            event.delete();
            List<EventLike> eventLikeList = eventLikeRepository.findByEventId(eventId);
            if (eventLikeList != null)
                eventLikeList.forEach(eventLikeRepository::delete);
        }
    }

    @Transactional
    public void proposeEvent(RequestEvent.ProposeEventDto proposeEventDto) {
        String memberId = getMemberId();
        Event event = getEvent(proposeEventDto.getEventId());
        if (!event.isCreator(memberId)) {
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }
        EventProposal eventProposal = eventProposalRepository.findByEventIdAndTruckId(proposeEventDto.getEventId(), proposeEventDto.getTruckId());
        if (eventProposal != null) {
            throw new CustomException(EventErrorCode.ALREADY_PROPOSED_TRUCK);
        }
        EventProposal newEventProposal = proposeEventDto.toEntity(event, getTruck(proposeEventDto.getTruckId()));
        eventProposalRepository.save(newEventProposal);
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

        EventApplication eventApplication = applyEventDto.toEntity(truck, event);
        eventApplication = eventApplicationRepository.save(eventApplication);

        for (String eventDateId : applyEventDto.getEventDateIdList()) {
            EventDate eventDate = eventDateRepository.findById(eventDateId)
                    .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_DATE_NOT_FOUND));

            EventApplicationDate eventApplicationDate = EventApplicationDate.builder().eventDate(eventDate).eventApplication(eventApplication).build();
            eventApplicationDateRepository.save(eventApplicationDate);
        }

        NotificationEvent.raise(new ApplicationReceivedNotificationEvent(event.getId(), event.getName(), eventApplication.getId()));
    }

    //TODO 한 번 처리(확정/거절)하고 난 후에는 안 되게 막을 것인지 확인 필요
    @Transactional
    public void handleEventTruck(RequestEvent.HandleEventTruckDto handleEventTruckDto) {
        EventTruck eventTruck = eventTruckRepository.findById(handleEventTruckDto.getEventTruckId())
                .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_TRUCK_NOT_FOUND));
        Event event = eventTruck.getEvent();
        TruckManager truckManager = truckManagerRepository.findByTruckIdAndMemberId(eventTruck.getTruck().getId(), getMemberId());
        if (truckManager == null) {
            throw new CustomException(TruckErrorCode.TRUCK_MANAGER_NOT_FOUND);
        }
        if (handleEventTruckDto.getEventTruckStatus().equals(EventTruckStatus.CONFIRMED)) {
            eventTruck.confirm();
            NotificationEvent.raise(new SelectionConfirmedNotificationEvent(event.getId(), event.getName(), eventTruck.getTruck().getName(), eventTruck.getId()));
        } else if (handleEventTruckDto.getEventTruckStatus().equals(EventTruckStatus.REJECTED)) {
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
        String memberId = getMemberId();
        EventNotice eventNotice = eventNoticeRepository.findEventNoticeForCreator(noticeId);
        if (eventNotice == null) {
            throw new CustomException(EventErrorCode.EVENT_NOTICE_NOT_FOUND);
        }
        if (eventNotice.getEvent().isCreator(memberId)) {
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }
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
}

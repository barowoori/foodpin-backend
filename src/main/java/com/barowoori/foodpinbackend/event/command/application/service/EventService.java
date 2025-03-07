package com.barowoori.foodpinbackend.event.command.application.service;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.event.command.application.dto.RequestEvent;
import com.barowoori.foodpinbackend.event.command.domain.exception.EventErrorCode;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.*;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.repository.FileRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private String getMemberId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    private Event getEvent(String eventId){
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(EventErrorCode.NOT_FOUND_EVENT));
    }

    @Transactional
    public void createEvent(RequestEvent.CreateEventDto createEventDto){
        String memberId = getMemberId();

        Event event = createEventDto.getEventInfoDto().toEntity(memberId);
        eventRepository.save(event);

        for (String fileId : createEventDto.getEventInfoDto().getFileIdList()){
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_PHOTO_NOT_FOUND));
            EventPhoto eventPhoto = EventPhoto.builder().file(file).updatedBy(memberId).event(event).build();
            eventPhotoRepository.save(eventPhoto);
        }

        EventView eventView = EventView.builder().event(event).build();
        eventViewRepository.save(eventView);
        event.initEventView(eventView);

        RegionInfo regionInfo = regionDoRepository.findByCode(createEventDto.getEventRegionDto().getRegionCode());
        EventRegion eventRegion = createEventDto.getEventRegionDto().toEntity(event, regionInfo);
        eventRegionRepository.save(eventRegion);
        event.initEventRegion(eventRegion);

        EventRecruitDetail eventRecruitDetail = createEventDto.getEventRecruitDto().toEntity(event);
        eventRecruitDetailRepository.save(eventRecruitDetail);
        event.initEventRecruitDetail(eventRecruitDetail);

        createEventDto.getEventDateDtoList().forEach(eventDateDto -> {
            EventDate eventDate = eventDateDto.toEntity(event);
            eventDateRepository.save(eventDate);
        });

        createEventDto.getEventCategoryDtoList().forEach(eventCategoryDto -> {
            Category category = categoryRepository.findByCode(eventCategoryDto.getCategoryCode());
            EventCategory eventCategory = eventCategoryDto.toEntity(event, category);
            eventCategoryRepository.save(eventCategory);
        });

        createEventDto.getEventDocumentDtoList().forEach(eventDocumentDto -> {
            EventDocument eventDocument = eventDocumentDto.toEntity(event);
            eventDocumentRepository.save(eventDocument);
        });

        eventRepository.save(event);
    }

    @Transactional
    public void updateEventInfo(String eventId, RequestEvent.UpdateEventInfoDto updateEventInfoDto){
        String memberId = getMemberId();
        Event event = getEvent(eventId);

        event.updateName(updateEventInfoDto.getName());
//        eventRepository.save(event);

        List<EventPhoto> photoList = eventPhotoRepository.findAllByEvent(event);
        photoList.forEach(eventPhotoRepository::delete);
        for (String fileId : updateEventInfoDto.getFileIdList()){
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_PHOTO_NOT_FOUND));
            EventPhoto eventPhoto = EventPhoto.builder().file(file).updatedBy(memberId).event(event).build();
            eventPhotoRepository.save(eventPhoto);
        }

        List<EventDate> eventDateList = eventDateRepository.findAllByEvent(event);
        eventDateList.forEach(eventDateRepository::delete);
        updateEventInfoDto.getEventDateDtoList().forEach(eventDateDto -> {
            EventDate eventDate = eventDateDto.toEntity(event);
            eventDateRepository.save(eventDate);
        });
        event.initEventRegion(null);
        EventRegion eventRegion = eventRegionRepository.findByEvent(event);
        eventRegionRepository.delete(eventRegion);
        RegionInfo regionInfo = regionDoRepository.findByCode(updateEventInfoDto.getEventRegionDto().getRegionCode());
        EventRegion updatedEventRegion = updateEventInfoDto.getEventRegionDto().toEntity(event, regionInfo);
        updatedEventRegion = eventRegionRepository.save(updatedEventRegion);
        event.initEventRegion(updatedEventRegion);

//        eventRepository.save(event);
    }

    @Transactional
    public void updateEventRecruit(String eventId, RequestEvent.UpdateEventRecruitDto updateEventRecruitDto){
        Event event = getEvent(eventId);

        EventRecruitDetail eventRecruitDetail = eventRecruitDetailRepository.findByEvent(event);
        eventRecruitDetailRepository.delete(eventRecruitDetail);
        EventRecruitDetail updatedEventRecruitDetail = updateEventRecruitDto.getEventRecruitDto().toEntity(event);
        eventRecruitDetailRepository.save(updatedEventRecruitDetail);
        event.initEventRecruitDetail(updatedEventRecruitDetail);

        eventRepository.save(event);
    }

    @Transactional
    public void updateEventDetail(String eventId, RequestEvent.UpdateEventDetailDto updateEventDetailDto){
        Event event = getEvent(eventId);

        List<EventCategory> eventCategoryList = eventCategoryRepository.findAllByEvent(event);
        eventCategoryList.forEach(eventCategoryRepository::delete);
        updateEventDetailDto.getEventCategoryDtoList().forEach(eventCategoryDto -> {
            Category category = categoryRepository.findByCode(eventCategoryDto.getCategoryCode());
            EventCategory eventCategory = eventCategoryDto.toEntity(event, category);
            eventCategoryRepository.save(eventCategory);
        });
        event.updateDescription(updateEventDetailDto.getDescription());
        event.updateGuidelines(updateEventDetailDto.getGuidelines());
        eventRepository.save(event);
    }

    @Transactional
    public void updateEventDocument(String eventId, RequestEvent.UpdateEventDocumentDto updateEventDocumentDto){
        Event event = getEvent(eventId);

        List<EventDocument> eventDocumentList = eventDocumentRepository.findAllByEvent(event);
        eventDocumentList.forEach(eventDocumentRepository::delete);
        updateEventDocumentDto.getEventDocumentDtoList().forEach(eventDocumentDto -> {
            EventDocument eventDocument = eventDocumentDto.toEntity(event);
            eventDocumentRepository.save(eventDocument);
        });
        event.updateSubmissionEmail(updateEventDocumentDto.getSubmissionEmail());
        event.updateDocumentSubmissionTarget(updateEventDocumentDto.getDocumentSubmissionTarget());
        eventRepository.save(event);
    }
}

package com.barowoori.foodpinbackend.event.command.application.service;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.event.command.application.dto.RequestEvent;
import com.barowoori.foodpinbackend.event.command.domain.exception.EventErrorCode;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.*;
import com.barowoori.foodpinbackend.truck.command.domain.exception.TruckErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventManagementService {
    private final EventRepository eventRepository;
    private final EventNoticeRepository eventNoticeRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final EventTruckRepository eventTruckRepository;
    private final EventDateRepository eventDateRepository;
    private final EventTruckDateRepository eventTruckDateRepository;

    private String getMemberId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    private Event getEvent(String eventId){
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(EventErrorCode.NOT_FOUND_EVENT));
    }

    @Transactional
    public void handleEventApplication(String eventApplicationId, RequestEvent.HandleEventApplicationDto handleEventApplicationDto){
        EventApplication eventApplication = eventApplicationRepository.findById(eventApplicationId)
                .orElseThrow(()-> new CustomException(EventErrorCode.EVENT_APPLICATION_NOT_FOUND));

        if (!eventApplication.getEvent().isCreator(getMemberId())){
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }

        if (!eventApplication.getStatus().equals(EventApplicationStatus.PENDING)){
            throw new CustomException(EventErrorCode.ALREADY_HANDLED_EVENT_APPLICATION);
        }

        if(!handleEventApplicationDto.getIsSelected()){
            eventApplication.reject();
            eventApplicationRepository.save(eventApplication);
            return;
        }
        if (Objects.equals(handleEventApplicationDto.getEventDateIdList(), null) || handleEventApplicationDto.getEventDateIdList().isEmpty()){
            throw new CustomException(EventErrorCode.EVENT_DATE_EMPTY);
        }

        EventTruck eventTruck = EventTruck.builder().event(eventApplication.getEvent()).truck(eventApplication.getTruck()).status(EventTruckStatus.PENDING).build();
        eventTruckRepository.save(eventTruck);

        List<EventDate> appliedDates = new ArrayList<>();
        eventApplication.getDates().forEach(eventApplicationDate -> {
            appliedDates.add(eventApplicationDate.getEventDate());
        });

        handleEventApplicationDto.getEventDateIdList().forEach(eventDateId -> {
            EventDate eventDate = eventDateRepository.findById(eventDateId)
                    .orElseThrow(() -> new CustomException(EventErrorCode.EVENT_DATE_NOT_FOUND));
            if (!appliedDates.contains(eventDate)) {
                throw new CustomException(EventErrorCode.EVENT_DATE_NOT_FOUND);
            }
            EventTruckDate eventTruckDate = EventTruckDate.builder().eventDate(eventDate).eventTruck(eventTruck).build();
            eventTruckDateRepository.save(eventTruckDate);
        });

        eventApplication.select();
        eventApplicationRepository.save(eventApplication);
    }

    //TODO EventStatus 중 SELECTING, IN_PROGRESS, COMPLETED는 선정 시작 또는 기한에 맞춰서 자동으로 변경되게 할 건지 확인 필요
    // 지금은 RECRUITING, RECRUITMENT_CANCELLED, RECRUITMENT_CLOSED만 있음
    @Transactional
    public void handleEventRecruitment(RequestEvent.HandleEventRecruitmentDto handleEventRecruitmentDto){
        Event event = getEvent(handleEventRecruitmentDto.getEventId());
        if (!event.isCreator(getMemberId())){
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }
        if (handleEventRecruitmentDto.getRecruitmentStatus().equals(EventStatus.RECRUITMENT_CLOSED)){
            event.updateStatus(EventStatus.RECRUITMENT_CLOSED);
        } else if (handleEventRecruitmentDto.getRecruitmentStatus().equals(EventStatus.RECRUITMENT_CANCELLED)) {
            event.updateStatus(EventStatus.RECRUITMENT_CANCELLED);
        } else throw new CustomException(EventErrorCode.WRONG_EVENT_RECRUITMENT_STATUS);
        eventRepository.save(event);
    }

    @Transactional
    public void readEventApplication(String eventApplicationId){
        EventApplication eventApplication = eventApplicationRepository.findById(eventApplicationId)
                .orElseThrow(()-> new CustomException(EventErrorCode.EVENT_APPLICATION_NOT_FOUND));
        // 행사 주최자인 경우에만 읽음 처리, 주최자가 아닌 경우에 예외까지 띄울 필요는 없을 것 같아서 추가하지 않음
        if (eventApplication.getEvent().isCreator(getMemberId())){
            eventApplication.read();
            eventApplicationRepository.save(eventApplication);
        }
    }

    @Transactional
    public void createEventNotice(RequestEvent.CreateEventNoticeDto createEventNoticeDto){
        Event event = getEvent(createEventNoticeDto.getEventId());
        String memberId = getMemberId();

        if (!event.isCreator(memberId)){
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }

        EventNotice eventNotice = createEventNoticeDto.toEntity(event);
        eventNoticeRepository.save(eventNotice);
    }

    @Transactional
    public void updateEventNotice(String eventNoticeId, RequestEvent.UpdateEventNoticeDto updateEventNoticeDto){
        EventNotice eventNotice = eventNoticeRepository.findById(eventNoticeId)
                .orElseThrow(()-> new CustomException(EventErrorCode.EVENT_NOTICE_NOT_FOUND));

        if (!eventNotice.getViews().isEmpty()){
            throw new CustomException(EventErrorCode.ALREADY_VIEWED_EVENT_NOTICE);
        }

        Event event = getEvent(eventNotice.getEvent().getId());
        String memberId = getMemberId();

        if (!event.isCreator(memberId)){
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }

        eventNotice.update(updateEventNoticeDto.getTitle(), updateEventNoticeDto.getContent());
        eventNoticeRepository.save(eventNotice);
    }

    @Transactional
    public void deleteEventNotice(String eventNoticeId){
        EventNotice eventNotice = eventNoticeRepository.findById(eventNoticeId)
                .orElseThrow(()-> new CustomException(EventErrorCode.EVENT_NOTICE_NOT_FOUND));
        Event event = getEvent(eventNotice.getEvent().getId());

        if (!eventNotice.getViews().isEmpty()){
            throw new CustomException(EventErrorCode.ALREADY_VIEWED_EVENT_NOTICE);
        }

        String memberId = getMemberId();

        if (!event.isCreator(memberId)){
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }

        eventNoticeRepository.delete(eventNotice);
    }
}
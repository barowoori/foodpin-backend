package com.barowoori.foodpinbackend.event.command.application.service;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.event.command.application.dto.RequestEvent;
import com.barowoori.foodpinbackend.event.command.domain.exception.EventErrorCode;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventManagementService {
    private final EventRepository eventRepository;
    private final EventNoticeRepository eventNoticeRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final EventTruckRepository eventTruckRepository;
    private final EventTruckDateRepository eventTruckDateRepository;

    private String getMemberId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    private Event getEvent(String eventId){
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(EventErrorCode.NOT_FOUND_EVENT));
    }

    @Transactional
    public void handleEventApplication(RequestEvent.HandleEventApplicationDto handleEventApplicationDto){
        EventApplication eventApplication = eventApplicationRepository.findById(handleEventApplicationDto.getEventApplicationId())
                .orElseThrow(()-> new CustomException(EventErrorCode.EVENT_APPLICATION_NOT_FOUND));

        if (!eventApplication.getEvent().getCreatedBy().equals(getMemberId())){
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }

        if (handleEventApplicationDto.getIsSelected()){
            if (!Objects.equals(handleEventApplicationDto.getDates(), null) && !handleEventApplicationDto.getDates().isEmpty()){
                EventTruck eventTruck = EventTruck.builder().event(eventApplication.getEvent()).truck(eventApplication.getTruck()).status(EventTruckStatus.PENDING).build();
                eventTruck = eventTruckRepository.save(eventTruck);
                List<LocalDate> selectedDates = handleEventApplicationDto.getDates();
                boolean isDateMatched = false;

                for (EventApplicationDate eventApplicationDate : eventApplication.getDates()){
                    if (selectedDates.contains(eventApplicationDate.getEventDate().getDate())){
                        EventTruckDate eventTruckDate = EventTruckDate.builder().eventDate(eventApplicationDate.getEventDate()).eventTruck(eventTruck).build();
                        eventTruckDateRepository.save(eventTruckDate);
                        isDateMatched = true;
                    }
                }
                if (!isDateMatched){
                    throw new CustomException(EventErrorCode.EVENT_DATE_NOT_FOUND);
                }
            } else throw new CustomException(EventErrorCode.EVENT_DATE_NOT_FOUND);
            eventApplication.select();
        } else {
            eventApplication.reject();
        }
        eventApplicationRepository.save(eventApplication);
    }

    @Transactional
    public void createEventNotice(RequestEvent.CreateEventNoticeDto createEventNoticeDto){
        Event event = getEvent(createEventNoticeDto.getEventId());
        String memberId = getMemberId();

        if (!event.getCreatedBy().equals(memberId)){
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }

        EventNotice eventNotice = createEventNoticeDto.toEntity(event);
        eventNoticeRepository.save(eventNotice);
    }

    @Transactional
    public void updateEventNotice(String eventNoticeId, RequestEvent.UpdateEventNoticeDto updateEventNoticeDto){
        EventNotice eventNotice = eventNoticeRepository.findById(eventNoticeId)
                .orElseThrow(()-> new CustomException(EventErrorCode.EVENT_NOTICE_NOT_FOUND));
        Event event = getEvent(eventNotice.getEvent().getId());
        String memberId = getMemberId();

        if (!event.getCreatedBy().equals(memberId)){
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
        String memberId = getMemberId();

        if (!event.getCreatedBy().equals(memberId)){
            throw new CustomException(EventErrorCode.NOT_EVENT_CREATOR);
        }

        eventNoticeRepository.delete(eventNotice);
    }
}
package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.event.command.domain.exception.EventErrorCode;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventDetail;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.model.EventLike;
import com.barowoori.foodpinbackend.member.command.domain.repository.EventLikeRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class EventDetailService {
    private final EventRepository eventRepository;
    private final ImageManager imageManager;
    private final EventRegionFullNameGenerator eventRegionFullNameGenerator;
    private final EventLikeRepository eventLikeRepository;

    public EventDetailService(EventRepository eventRepository, ImageManager imageManager, EventRegionFullNameGenerator eventRegionFullNameGenerator,
                              EventLikeRepository eventLikeRepository) {
        this.eventRepository = eventRepository;
        this.imageManager = imageManager;
        this.eventRegionFullNameGenerator = eventRegionFullNameGenerator;
        this.eventLikeRepository = eventLikeRepository;
    }

    @Transactional
    public EventDetail getEventDetail(String memberId, String eventId) {
        Event event = eventRepository.findEventDetail(eventId);
        if (event == null || event.getIsDeleted()){
            throw new CustomException(EventErrorCode.NOT_FOUND_EVENT);
        }
        List<RegionCode> regionNames = eventRegionFullNameGenerator.findRegionCodesByEventId(eventId);
        String regionList = eventRegionFullNameGenerator.makeRegionList(regionNames);
        EventLike eventLike = eventLikeRepository.findByMemberIdAndEventId(memberId, eventId);
        event.getView().addViews();
        return EventDetail.of(event, memberId, eventLike != null, imageManager, regionNames, regionList);
    }
}

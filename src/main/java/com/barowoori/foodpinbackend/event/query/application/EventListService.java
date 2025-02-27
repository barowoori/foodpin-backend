package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRegionRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventList;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class EventListService {
    private final EventRepository eventRepository;
    private final RegionDoRepository regionDoRepository;
    private final ImageManager imageManager;
    private final EventRegionRepository eventRegionRepository;
    private final EventRegionFullNameGenerator eventRegionFullNameGenerator;

    public EventListService(EventRepository eventRepository, RegionDoRepository regionDoRepository, ImageManager imageManager, EventRegionRepository eventRegionRepository,
                            EventRegionFullNameGenerator eventRegionFullNameGenerator) {
        this.eventRepository = eventRepository;
        this.regionDoRepository = regionDoRepository;
        this.imageManager = imageManager;
        this.eventRegionRepository = eventRegionRepository;
        this.eventRegionFullNameGenerator = eventRegionFullNameGenerator;
    }

    @Transactional(readOnly = true)
    public Page<EventList> findEventList(String searchTerm, List<String> regionCodes,
                                         LocalDate startDate, LocalDate endDate,
                                         List<String> categoryCodes, Pageable pageable) {
        Map<RegionType, List<String>> regionIds = regionDoRepository.findRegionIdsByFilter(regionCodes);
        Page<Event> events = eventRepository.findEventListByFilter(searchTerm, regionIds, startDate, endDate, categoryCodes, pageable);
        List<String> eventIds = events.map(Event::getId).stream().toList();
        Map<String, List<String>> regionNames = eventRegionFullNameGenerator.findRegionNamesByEventIds(eventIds);

        return events.map(event -> EventList.of(event, regionNames.get(event.getId()), imageManager));
    }
}

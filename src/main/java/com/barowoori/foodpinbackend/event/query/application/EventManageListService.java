package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventManageList;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EventManageListService {
    private final EventRepository eventRepository;
    private final ImageManager imageManager;
    private final EventRegionFullNameGenerator eventRegionFullNameGenerator;

    public EventManageListService(EventRepository eventRepository, ImageManager imageManager, EventRegionFullNameGenerator eventRegionFullNameGenerator) {
        this.eventRepository = eventRepository;
        this.imageManager = imageManager;
        this.eventRegionFullNameGenerator = eventRegionFullNameGenerator;
    }

    public Page<EventManageList> findProgressEventManageList(String memberId, String status, Pageable pageable) {
        Page<Event> events = eventRepository.findProgressEventManageList(memberId, status, pageable);
        List<String> eventIds = events.map(Event::getId).stream().toList();
        Map<String, List<String>> regionNames = eventRegionFullNameGenerator.findRegionNamesByEventIds(eventIds);
        return events.map(event -> EventManageList.of(event, regionNames.get(event.getId()), imageManager));
    }

    public Page<EventManageList> findCompletedEventManageList(String memberId, String status, Pageable pageable) {
        Page<Event> events = eventRepository.findCompletedEventManageList(memberId, status, pageable);
        List<String> eventIds = events.map(Event::getId).stream().toList();
        Map<String, List<String>> regionNames = eventRegionFullNameGenerator.findRegionNamesByEventIds(eventIds);
        return events.map(event -> EventManageList.of(event, regionNames.get(event.getId()), imageManager));
    }
}

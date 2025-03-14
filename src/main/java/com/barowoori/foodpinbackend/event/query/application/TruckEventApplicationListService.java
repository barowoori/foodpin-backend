package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import org.springframework.stereotype.Component;

@Component
public class TruckEventApplicationListService {
    private final EventApplicationRepository eventApplicationRepository;
    private final ImageManager imageManager;
    private final EventRegionFullNameGenerator eventRegionFullNameGenerator;

    public TruckEventApplicationListService(EventApplicationRepository eventApplicationRepository, ImageManager imageManager, EventRegionFullNameGenerator eventRegionFullNameGenerator) {
        this.eventApplicationRepository = eventApplicationRepository;
        this.imageManager = imageManager;
        this.eventRegionFullNameGenerator = eventRegionFullNameGenerator;
    }

}

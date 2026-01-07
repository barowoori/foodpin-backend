package com.barowoori.foodpinbackend.notification.command.domain.model.event;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRegion;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class InterestRegisteredNotificationEvent extends NotificationEvent {
    private final String eventId;
    private final String eventName;
    private final String eventRegionName;
    private final Map<RegionType, String> regionIds;
    private final List<Category> categories;

    public InterestRegisteredNotificationEvent(String eventId, String eventName, String eventRegionName, Map<RegionType, String> regionIds, List<Category> categories) {
        super();
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventRegionName = eventRegionName;
        this.regionIds = regionIds;
        this.categories = categories;
    }
}

package com.barowoori.foodpinbackend.notification.command.domain.model.event;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRegion;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

import java.util.List;

@Getter
public class InterestRegisteredNotificationEvent extends NotificationEvent {
    private final String eventId;
    private final String eventName;
    private final String eventRegionName;
    private final EventRegion eventRegion;
    private final List<Category> categories;

    public InterestRegisteredNotificationEvent(String eventId, String eventName, String eventRegionName, EventRegion eventRegion, List<Category> categories) {
        super();
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventRegionName = eventRegionName;
        this.eventRegion = eventRegion;
        this.categories = categories;
    }
}

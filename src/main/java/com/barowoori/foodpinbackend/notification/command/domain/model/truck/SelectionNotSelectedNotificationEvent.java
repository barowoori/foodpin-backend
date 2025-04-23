package com.barowoori.foodpinbackend.notification.command.domain.model.truck;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class SelectionNotSelectedNotificationEvent extends NotificationEvent {
    private final String eventId;
    private final String eventName;
    private final String eventApplicationId;

    public SelectionNotSelectedNotificationEvent(String eventId, String eventName, String eventApplicationId) {
        super();
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventApplicationId = eventApplicationId;
    }
}

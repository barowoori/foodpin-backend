package com.barowoori.foodpinbackend.notification.command.domain.model.truck;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class EventCastedNotificationEvent extends NotificationEvent {
    private final String eventId;
    private final String truckId;
    private final String eventName;

    public EventCastedNotificationEvent(String eventId, String truckId, String eventName) {
        super();
        this.eventId = eventId;
        this.truckId = truckId;
        this.eventName = eventName;
    }
}

package com.barowoori.foodpinbackend.notification.command.domain.model.truck;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;

public class TruckEventCastedNotificationEvent extends NotificationEvent {
    private final String eventId;
    private final String eventName;

    public TruckEventCastedNotificationEvent(String eventId, String eventName) {
        super();
        this.eventId = eventId;
        this.eventName = eventName;
    }
}

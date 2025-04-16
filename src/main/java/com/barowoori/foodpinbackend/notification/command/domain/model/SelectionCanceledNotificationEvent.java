package com.barowoori.foodpinbackend.notification.command.domain.model;

import lombok.Getter;

@Getter
public class SelectionCanceledNotificationEvent extends NotificationEvent{
    private final String eventId;
    private final String eventName;
    private final String truckName;

    public SelectionCanceledNotificationEvent(String eventId, String eventName, String truckName) {
        super();
        this.eventId = eventId;
        this.eventName = eventName;
        this.truckName = truckName;
    }
}

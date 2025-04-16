package com.barowoori.foodpinbackend.notification.command.domain.model;

import lombok.Getter;

@Getter
public class SelectionConfirmedNotificationEvent extends NotificationEvent{
    private final String eventId;
    private final String eventName;
    private final String truckName;
    private final String eventTruckId;

    public SelectionConfirmedNotificationEvent(String eventId, String eventName, String truckName, String eventTruckId) {
        super();
        this.eventId = eventId;
        this.eventName = eventName;
        this.truckName = truckName;
        this.eventTruckId =eventTruckId;
    }
}

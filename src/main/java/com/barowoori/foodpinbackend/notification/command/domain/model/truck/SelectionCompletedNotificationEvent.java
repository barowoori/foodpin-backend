package com.barowoori.foodpinbackend.notification.command.domain.model.truck;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class SelectionCompletedNotificationEvent extends NotificationEvent {
    private final String eventId;
    private final String eventName;
    private final String eventTruckId;
    private final String truckId;

    public SelectionCompletedNotificationEvent(String eventId,String eventTruckId, String eventName, String truckId) {
        super();
        this.eventId = eventId;
        this.eventTruckId = eventTruckId;
        this.eventName = eventName;
        this.truckId = truckId;
    }
}

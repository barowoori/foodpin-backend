package com.barowoori.foodpinbackend.notification.command.domain.model.truck;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class TruckSelectionConfirmedNotificationEvent extends NotificationEvent {
    private final String eventId;
    private final String eventName;
    private final String eventTruckId;
    private final String truckId;

    public TruckSelectionConfirmedNotificationEvent(String eventId, String eventName, String eventTruckId, String truckId) {
        super();
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventTruckId = eventTruckId;
        this.truckId = truckId;
    }
}

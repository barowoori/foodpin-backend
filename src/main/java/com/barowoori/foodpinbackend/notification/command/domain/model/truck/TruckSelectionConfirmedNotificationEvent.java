package com.barowoori.foodpinbackend.notification.command.domain.model.truck;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class TruckSelectionConfirmedNotificationEvent extends NotificationEvent {
    private final String eventId;
    private final String eventTruckId;

    public TruckSelectionConfirmedNotificationEvent(String eventId, String eventTruckId) {
        super();
        this.eventId = eventId;
        this.eventTruckId = eventTruckId;
    }
}

package com.barowoori.foodpinbackend.notification.command.domain.model;

import lombok.Getter;

@Getter
public class ApplicationReceivedNotificationEvent extends NotificationEvent {
    private final String eventId;
    private final String eventName;
    private final String eventApplicationId;

    public ApplicationReceivedNotificationEvent(String eventId, String eventName, String eventApplicationId){
        super();
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventApplicationId = eventApplicationId;
    }
}

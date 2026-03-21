package com.barowoori.foodpinbackend.notification.command.domain.model.truck;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class BusinessRegistrationApprovedNotificationEvent extends NotificationEvent {
    private final String truckId;
    private final String truckName;

    public BusinessRegistrationApprovedNotificationEvent(String truckId, String truckName) {
        super();
        this.truckId = truckId;
        this.truckName = truckName;
    }
}

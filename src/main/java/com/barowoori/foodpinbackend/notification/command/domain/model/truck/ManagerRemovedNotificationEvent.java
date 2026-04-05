package com.barowoori.foodpinbackend.notification.command.domain.model.truck;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class ManagerRemovedNotificationEvent extends NotificationEvent {
    private final String truckId;
    private final String truckName;
    private final String memberId;

    public ManagerRemovedNotificationEvent(String truckName, String memberId, String truckId) {
        super();
        this.truckId = truckId;
        this.truckName = truckName;
        this.memberId = memberId;
    }
}

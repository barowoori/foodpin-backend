package com.barowoori.foodpinbackend.notification.command.domain.model.truck;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class ManagerRemovedNotificationEvent extends NotificationEvent {
    private final String truckName;
    private final String memberId;

    public ManagerRemovedNotificationEvent(String truckName, String memberId) {
        super();
        this.truckName = truckName;
        this.memberId = memberId;
    }
}

package com.barowoori.foodpinbackend.notification.command.domain.model.truck;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class ManagerAddedNotificationEvent extends NotificationEvent {
    private final String truckId;
    private final String truckName;
    private final String nickname;

    public ManagerAddedNotificationEvent(String truckId, String truckName, String nickname) {
        super();
        this.truckId = truckId;
        this.truckName = truckName;
        this.nickname = nickname;
    }
}

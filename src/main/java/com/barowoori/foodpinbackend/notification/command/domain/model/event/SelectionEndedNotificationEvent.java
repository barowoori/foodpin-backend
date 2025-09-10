package com.barowoori.foodpinbackend.notification.command.domain.model.event;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class SelectionEndedNotificationEvent extends NotificationEvent {

    public SelectionEndedNotificationEvent() {
        super();
    }
}

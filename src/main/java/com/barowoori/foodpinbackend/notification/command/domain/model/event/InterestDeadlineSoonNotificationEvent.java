package com.barowoori.foodpinbackend.notification.command.domain.model.event;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class InterestDeadlineSoonNotificationEvent extends NotificationEvent {

    public InterestDeadlineSoonNotificationEvent() {
        super();
    }
}

package com.barowoori.foodpinbackend.notification.command.domain.model;

import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;

@Getter
public abstract class NotificationEvent {
    private static ApplicationEventPublisher publisher;

    public NotificationEvent() {
    }

    public static void setPublisher(ApplicationEventPublisher publisher) {
        NotificationEvent.publisher = publisher;
    }

    public static void raise(Object event) {
        if (publisher != null) {
            publisher.publishEvent(event);
        }
    }
}

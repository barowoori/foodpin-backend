package com.barowoori.foodpinbackend.notification.command.domain.model.truck;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import lombok.Getter;

@Getter
public class EventNoticePostedNotificationEvent extends NotificationEvent {
    private final String eventId;
    private final String eventName;
    private final String noticeId;

    public EventNoticePostedNotificationEvent(String eventId, String eventName, String noticeId) {
        super();
        this.eventId = eventId;
        this.eventName = eventName;
        this.noticeId = noticeId;
    }
}

package com.barowoori.foodpinbackend.notification;

import com.barowoori.foodpinbackend.notification.command.domain.model.ApplicationReceivedNotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationEvent;
import com.barowoori.foodpinbackend.notification.command.domain.model.SelectionCanceledNotificationEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class EventNotificationEventHandlerTests {
    @Autowired
    private ApplicationEventPublisher publisher;

    @Test
    void ApplicationReceivedNotificationEventTest() {
        ApplicationReceivedNotificationEvent event = new ApplicationReceivedNotificationEvent("행사id", "행사명이지렁", "행사지원id");
        NotificationEvent.raise(event);
    }

    @Test
    void SelectionCancelNotificationEventTest() {
        SelectionCanceledNotificationEvent event = new SelectionCanceledNotificationEvent("행사id", "행사명이지렁", "트럭명이야");
        NotificationEvent.raise(event);
    }

}

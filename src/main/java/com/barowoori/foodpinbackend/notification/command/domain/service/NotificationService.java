package com.barowoori.foodpinbackend.notification.command.domain.service;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationTargetType;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationType;

public interface NotificationService {
    void pushAlarmToToken(NotificationType type, String title, String content, String token, NotificationTargetType targetType, String targetId);
}

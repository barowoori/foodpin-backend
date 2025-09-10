package com.barowoori.foodpinbackend.pushAlarmHistory.command.application.dto;

import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationTargetType;
import com.barowoori.foodpinbackend.notification.command.domain.model.NotificationType;
import com.barowoori.foodpinbackend.pushAlarmHistory.command.domain.model.PushAlarmHistory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class ResponsePushAlarmHistory {
    @Data
    @Builder
    public static class GetPushAlarmHistory{
        private String title;
        private String content;
        private NotificationType type;
        private NotificationTargetType notificationTargetType;
        private String targetId;
        private LocalDateTime createdAt;

        public static GetPushAlarmHistory of(PushAlarmHistory pushAlarmHistory){
            return GetPushAlarmHistory.builder()
                    .title(pushAlarmHistory.getNotificationType().getName())
                    .content(pushAlarmHistory.getContent())
                    .type(pushAlarmHistory.getNotificationType())
                    .notificationTargetType(pushAlarmHistory.getNotificationTargetType())
                    .targetId(pushAlarmHistory.getTargetId())
                    .createdAt(pushAlarmHistory.getCreateAt())
                    .build();
        }
    }

}

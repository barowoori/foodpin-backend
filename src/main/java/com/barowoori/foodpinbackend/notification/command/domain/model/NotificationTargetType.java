package com.barowoori.foodpinbackend.notification.command.domain.model;

public enum NotificationTargetType {
    EVENT_APPLICATION_DETAIL("행사 지원 상세 정보");

    private final String name;

    NotificationTargetType(String name){
        this.name = name;
    }
}

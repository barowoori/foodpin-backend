package com.barowoori.foodpinbackend.notification.command.domain.model;

public enum NotificationTargetType {
    EVENT_APPLICATION_DETAIL("행사 지원 상세 정보"),
    EVENT_APPLICATION_SELECTED_LIST("선정자 목록"),
    EVENT_APPLICATION_LIST("지원자 목록");

    private final String name;

    NotificationTargetType(String name){
        this.name = name;
    }
}

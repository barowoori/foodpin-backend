package com.barowoori.foodpinbackend.notification.command.domain.model;

public enum NotificationTargetType {
    EVENT_APPLICATION_DETAIL("행사 지원 상세 정보"),
    EVENT_APPLICATION_SELECTED_LIST("선정자 목록"),
    EVENT_APPLICATION_LIST("지원자 목록"),
    TRUCK_SELECTED_EVENT_LIST("선정된 행사 목록"),
    EVENT_DETAIL("행사 상세"),
    NONE("없음"),
    EVENT_NOTICE_DETAIL("행사 공지사항 상세"),
    TRUCK_MANAGER_LIST("트럭 운영자 목록"),
    EVENT_MANAGEMENT_LIST("행사 관리 목록");

    private final String name;

    NotificationTargetType(String name){
        this.name = name;
    }
}

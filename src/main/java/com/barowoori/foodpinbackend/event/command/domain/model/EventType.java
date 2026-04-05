package com.barowoori.foodpinbackend.event.command.domain.model;

import lombok.Getter;

@Getter
public enum EventType {
    CORPORATE("기업행사"),
    PERSONAL("개인행사"),
    SCHOOL("학교행사"),
    LOCAL("지역행사"),
    APARTMENT_MARKET("아파트 장터"),
    CELEBRITY_SUPPORT("연예인 서포트");

    private final String label;

    EventType(String label) {
        this.label = label;
    }
}

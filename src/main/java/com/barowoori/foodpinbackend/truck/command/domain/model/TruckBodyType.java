package com.barowoori.foodpinbackend.truck.command.domain.model;

import lombok.Getter;

@Getter
public enum TruckBodyType {
    WING_BODY("윙바디형"),
    STANDARD("기본 탑차형"),
    OTHER("기타");

    TruckBodyType(String label) {
        this.label = label;
    }

    private final String label;

}

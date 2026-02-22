package com.barowoori.foodpinbackend.event.command.domain.model;

import lombok.Getter;

@Getter
public enum PriceRange {
    UNDER_7000("7천원 미만"),
    UNDER_8000("8천원 미만"),
    UNDER_9000("9천원 미만"),
    UNDER_10000("1만원 미만"),
    NO_MATTER("상관없음");

    private final String label;

    PriceRange(String label) {
        this.label = label;
    }
}

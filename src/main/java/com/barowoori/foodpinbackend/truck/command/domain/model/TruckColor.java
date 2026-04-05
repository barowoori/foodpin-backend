package com.barowoori.foodpinbackend.truck.command.domain.model;

import lombok.Getter;

@Getter
public enum TruckColor {
    RED("빨간색"),
    ORANGE("주황색"),
    YELLOW("노란색"),
    LIGHT_GREEN("연두색"),
    GREEN("초록색"),
    SKY_BLUE("하늘색"),
    BLUE("파란색"),

    MINT("민트색"),
    NAVY("남색"),
    PURPLE("보라색"),
    PINK("핑크색"),
    BROWN("갈색"),
    BLACK("검은색"),
    WHITE("흰색");

    TruckColor(String label) {
        this.label = label;
    }

    private final String label;
}

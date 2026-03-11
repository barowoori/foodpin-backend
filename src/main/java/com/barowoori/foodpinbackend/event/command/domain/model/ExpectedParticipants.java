package com.barowoori.foodpinbackend.event.command.domain.model;

import lombok.Getter;

@Getter
public enum ExpectedParticipants {

    UNDECIDED("미정"),
    UNDER_50("50명 미만"),
    UNDER_100("100명 미만"),
    UNDER_150("150명 미만"),
    UNDER_200("200명 미만"),
    OVER_200("200명 이상");

    private final String label;

    ExpectedParticipants(String label) {
        this.label = label;
    }
}

package com.barowoori.foodpinbackend.event.command.domain.model;

import lombok.Getter;

@Getter
public enum EventApplicationStatus {
    PENDING, SELECTED, REJECTED;
}

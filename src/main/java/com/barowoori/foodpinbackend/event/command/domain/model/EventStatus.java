package com.barowoori.foodpinbackend.event.command.domain.model;

import lombok.Getter;

@Getter
public enum EventStatus {
    IN_PROGRESS, CLOSED, CANCELED
}

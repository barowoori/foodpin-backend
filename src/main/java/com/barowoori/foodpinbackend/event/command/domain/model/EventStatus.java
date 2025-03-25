package com.barowoori.foodpinbackend.event.command.domain.model;

import lombok.Getter;

@Getter
public enum EventStatus {
    RECRUITING, SELECTING, IN_PROGRESS, COMPLETED, RECRUITMENT_CANCELLED, RECRUITMENT_CLOSED
}

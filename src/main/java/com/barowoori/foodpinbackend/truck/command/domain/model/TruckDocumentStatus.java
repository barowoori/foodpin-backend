package com.barowoori.foodpinbackend.truck.command.domain.model;

import lombok.Getter;

@Getter
public enum TruckDocumentStatus {
    PENDING, APPROVED, REJECTED;
}

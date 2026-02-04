package com.barowoori.foodpinbackend.truck.command.domain.model;

import lombok.Getter;

@Getter
public enum TruckType {
    SNACK("간식차"),
    MEAL("식사차"),
    STREET_FOOD("분식차"),
    COFFEE("커피차");

    TruckType(String label){
        this.label = label;
    }

    private final String label;

}

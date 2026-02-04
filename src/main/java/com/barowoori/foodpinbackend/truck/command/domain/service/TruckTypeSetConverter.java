package com.barowoori.foodpinbackend.truck.command.domain.service;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckType;
import jakarta.persistence.Converter;

@Converter
public class TruckTypeSetConverter
        extends AbstractEnumSetConverter<TruckType> {

    public TruckTypeSetConverter() {
        super(TruckType.class);
    }
}
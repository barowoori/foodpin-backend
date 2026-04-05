package com.barowoori.foodpinbackend.truck.command.domain.service;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckColor;
import jakarta.persistence.Converter;

@Converter
public class TruckColorSetConverter
        extends AbstractEnumSetConverter<TruckColor> {

    public TruckColorSetConverter() {
        super(TruckColor.class);
    }
}
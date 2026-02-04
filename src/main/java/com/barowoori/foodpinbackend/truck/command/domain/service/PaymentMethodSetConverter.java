package com.barowoori.foodpinbackend.truck.command.domain.service;

import com.barowoori.foodpinbackend.truck.command.domain.model.PaymentMethod;
import jakarta.persistence.Converter;

@Converter
public class PaymentMethodSetConverter
        extends AbstractEnumSetConverter<PaymentMethod> {

    public PaymentMethodSetConverter() {
        super(PaymentMethod.class);
    }
}
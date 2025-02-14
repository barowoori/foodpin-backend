package com.barowoori.foodpinbackend.document.command.domain.service;

import java.time.LocalDate;

public interface BusinessNumberValidator {
    Boolean validate(String businessNumber, String representativeName, LocalDate openingDate);
}

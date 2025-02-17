package com.barowoori.foodpinbackend.document.command.application.service;

import com.barowoori.foodpinbackend.document.command.application.dto.ResponseDocument;
import com.barowoori.foodpinbackend.document.command.domain.service.BusinessNumberValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DocumentService {
    private final BusinessNumberValidator businessNumberValidator;

    public DocumentService(BusinessNumberValidator businessNumberValidator) {
        this.businessNumberValidator = businessNumberValidator;
    }

    public ResponseDocument.validDto validateBusinessNumber(String businessNumber, String representativeName, LocalDate openingDate) {
        return ResponseDocument.validDto.toDto(businessNumberValidator.validate(businessNumber, representativeName, openingDate));
    }
}

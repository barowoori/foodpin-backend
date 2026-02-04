package com.barowoori.foodpinbackend.truck.command.domain.service;

import com.barowoori.foodpinbackend.truck.command.domain.model.ProofIssuanceType;
import jakarta.persistence.Converter;

@Converter
public class ProofIssuanceTypeSetConverter
        extends AbstractEnumSetConverter<ProofIssuanceType> {

    public ProofIssuanceTypeSetConverter() {
        super(ProofIssuanceType.class);
    }
}
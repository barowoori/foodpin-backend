package com.barowoori.foodpinbackend.truck.command.domain.repository.dto;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TruckDocumentInfoDto {
    private DocumentType type;
    private TruckDocumentStatus status;
}

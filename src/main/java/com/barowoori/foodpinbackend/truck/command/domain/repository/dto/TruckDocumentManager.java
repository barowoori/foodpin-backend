package com.barowoori.foodpinbackend.truck.command.domain.repository.dto;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import lombok.Getter;

import java.util.List;
@Getter
public class TruckDocumentManager {
    private List<TruckDocument> documents;

    protected TruckDocumentManager() {
    }

    public TruckDocumentManager(List<TruckDocument> documents) {
        this.documents = documents;
    }

    public boolean hasDocumentType(DocumentType type) {
        return documents.stream().anyMatch(document -> document.getType().equals(type));
    }

    public List<TruckDocument> getDocumentsByType(DocumentType type) {
        return documents.stream().filter(document -> document.getType().equals(type)).toList();
    }

    public List<DocumentType> getTypes() {
        return documents.stream().map(TruckDocument::getType).distinct().toList();
    }
}

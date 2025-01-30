package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDocumentRepository extends JpaRepository<EventDocument, String> {
}

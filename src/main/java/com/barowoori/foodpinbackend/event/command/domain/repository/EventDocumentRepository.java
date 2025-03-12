package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventDocumentRepository extends JpaRepository<EventDocument, String> {
    List<EventDocument> findByEventId(String eventId);
}

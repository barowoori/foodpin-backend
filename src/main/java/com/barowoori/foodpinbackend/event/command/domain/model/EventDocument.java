package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name = "event_documents")
@Getter
public class EventDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "events_id")
    private Event event;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private DocumentType type;

    protected EventDocument(){}

    @Builder
    public EventDocument(Event event, DocumentType type) {
        this.event = event;
        this.type = type;
    }
}

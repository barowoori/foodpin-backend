package com.barowoori.foodpinbackend.event.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_application_dates")
@Getter
public class EventApplicationDate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "event_dates_id")
    private EventDate eventDate;

    @ManyToOne
    @JoinColumn(name = "event_applications_id")
    private EventApplication eventApplication;

    @Builder
    public EventApplicationDate(EventDate eventDate, EventApplication eventApplication) {
        this.eventDate = eventDate;
        this.eventApplication = eventApplication;
    }
}

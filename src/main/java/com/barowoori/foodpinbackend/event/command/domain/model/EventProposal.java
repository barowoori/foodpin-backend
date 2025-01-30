package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_proposals")
@Getter
public class EventProposal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "events_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "trucks_id")
    private Truck truck;

    protected EventProposal(){}

    @Builder
    public EventProposal(Event event, Truck truck) {
        this.event = event;
        this.truck = truck;
    }
}

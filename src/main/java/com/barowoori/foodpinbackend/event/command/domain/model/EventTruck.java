package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_trucks")
@Getter
public class EventTruck {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trucks_id")
    private Truck truck;

    protected EventTruck(){}

    @Builder
    public EventTruck(Event event, Truck truck) {
        this.event = event;
        this.truck = truck;
    }
}

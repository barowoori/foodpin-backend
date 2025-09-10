package com.barowoori.foodpinbackend.event.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_truck_dates")
@Getter
public class EventTruckDate {
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
    @JoinColumn(name = "event_trucks_id")
    private EventTruck eventTruck;

    protected EventTruckDate(){}

    @Builder
    public EventTruckDate(EventDate eventDate, EventTruck eventTruck) {
        this.eventDate = eventDate;
        this.eventTruck = eventTruck;
    }
}

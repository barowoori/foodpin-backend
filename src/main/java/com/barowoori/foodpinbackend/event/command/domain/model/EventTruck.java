package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'PENDING'")
    private EventTruckStatus status;

    @OneToMany(mappedBy = "eventTruck")
    private List<EventTruckDate> dates = new ArrayList<>();

    protected EventTruck() {
    }

    @Builder
    public EventTruck(Event event, Truck truck, EventTruckStatus status) {
        this.event = event;
        this.truck = truck;
        this.status = status;
    }

    public void confirm(){
        this.status = EventTruckStatus.CONFIRMED;
    }

    public void reject(){
        this.status = EventTruckStatus.REJECTED;
    }
}

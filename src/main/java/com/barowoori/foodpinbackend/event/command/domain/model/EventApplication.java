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
@Table(name = "event_applications")
@Getter
public class EventApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "trucks_id")
    private Truck truck;

    @ManyToOne
    @JoinColumn(name = "events_id")
    private Event event;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'PENDING'")
    private EventApplicationStatus status;

    @Column(name = "is_read")
    @ColumnDefault("0")
    private Boolean isRead;

    @OneToMany(mappedBy = "eventApplication")
    private List<EventApplicationDate> dates = new ArrayList<>();


    protected EventApplication() {
    }

    @Builder
    public EventApplication(Truck truck, Event event, EventApplicationStatus status, Boolean isRead) {
        this.truck = truck;
        this.event = event;
        this.status = status;
        this.isRead = isRead;
    }

    public void select() {
        this.status = EventApplicationStatus.SELECTED;
    }

    public void reject() {
        this.status = EventApplicationStatus.REJECTED;
    }

    public void read() {
        this.isRead = Boolean.TRUE;
    }
}

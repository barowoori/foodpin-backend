package com.barowoori.foodpinbackend.event.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "event_dates")
@Getter
public class EventDate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "event_date")
    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_id")
    private Event event;

    protected EventDate(){
    }

    @Builder
    public EventDate(LocalDate date, LocalTime startTime, LocalTime endTime, Event event) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.event = event;
    }
}

package com.barowoori.foodpinbackend.event.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@Getter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "name")
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private EventStatus status;

    @Column(name = "status_comment")
    private String statusComment;

    @OneToOne(mappedBy = "event")
    private EventView view;

    @OneToOne(mappedBy = "event")
    private EventDetail eventDetail;

    @OneToOne(mappedBy = "event")
    private EventRecruitDetail recruitDetail;

    protected Event(){}

    @Builder
    public Event(String createdBy, String name, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        this.createdBy = createdBy;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

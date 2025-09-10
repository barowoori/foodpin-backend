package com.barowoori.foodpinbackend.event.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_notice_views")
@Getter
public class EventNoticeView {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_notices_id")
    private EventNotice eventNotice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_trucks_id")
    private EventTruck eventTruck;

    protected EventNoticeView(){}

    @Builder
    public EventNoticeView(EventNotice eventNotice, EventTruck eventTruck) {
        this.eventNotice = eventNotice;
        this.eventTruck = eventTruck;
    }
}

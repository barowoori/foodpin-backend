package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.truck.command.domain.model.AccessStatus;
import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_contact_access_log")
public class EventContactAccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "member_id")
    private String memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_status")
    private AccessStatus accessStatus;

    @Column(name = "failure_reason")
    private String failureReason;

    @Builder
    public EventContactAccessLog(String eventId, String memberId, AccessStatus accessStatus, String failureReason) {
        this.eventId = eventId;
        this.memberId = memberId;
        this.accessStatus = accessStatus;
        this.failureReason = failureReason;
    }
}

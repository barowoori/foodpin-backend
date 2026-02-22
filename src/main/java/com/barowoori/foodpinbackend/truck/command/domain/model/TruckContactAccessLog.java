package com.barowoori.foodpinbackend.truck.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "truck_contact_access_log")
public class TruckContactAccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "truck_id")
    private String truckId;

    @Column(name = "member_id")
    private String memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_status")
    private AccessStatus accessStatus;

    @Column(name = "failure_reason")
    private String failureReason;

    @Builder
    public TruckContactAccessLog(String truckId, String memberId, AccessStatus accessStatus, String failureReason) {
        this.truckId = truckId;
        this.memberId = memberId;
        this.accessStatus = accessStatus;
        this.failureReason = failureReason;
    }
}

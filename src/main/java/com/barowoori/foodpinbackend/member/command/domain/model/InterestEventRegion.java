package com.barowoori.foodpinbackend.member.command.domain.model;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "interest_event_regions")
@Getter
public class InterestEventRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "region_type", nullable = false)
    private RegionType regionType;

    @Column(name = "region_id", nullable = false)
    private String regionId;

    @ManyToOne
    @JoinColumn(name = "interest_events_id", nullable = false)
    private InterestEvent interestEvent;

    protected InterestEventRegion() {
    }

    @Builder
    public InterestEventRegion(RegionType regionType, String regionId, InterestEvent interestEvent) {
        this.regionType = regionType;
        this.regionId = regionId;
        this.interestEvent = interestEvent;
    }
}

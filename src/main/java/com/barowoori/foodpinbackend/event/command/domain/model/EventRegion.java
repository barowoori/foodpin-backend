package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_regions")
@Getter
public class EventRegion {
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
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    protected EventRegion(){}

    @Builder
    public EventRegion(RegionType regionType, String regionId, Event event) {
        this.regionType = regionType;
        this.regionId = regionId;
        this.event = event;
    }
}

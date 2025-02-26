package com.barowoori.foodpinbackend.truck.command.domain.model;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "truck_regions")
@Getter
public class TruckRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "region_type", nullable = false)
    private RegionType regionType;

    @Column(name = "region_id", nullable = false)
    private String regionId;

    @ManyToOne
    @JoinColumn(name = "trucks_id", nullable = false)
    private Truck truck;

    protected TruckRegion(){}

    @Builder
    public TruckRegion(RegionType regionType, String regionId, Truck truck) {
        this.regionType = regionType;
        this.regionId = regionId;
        this.truck = truck;
    }
}

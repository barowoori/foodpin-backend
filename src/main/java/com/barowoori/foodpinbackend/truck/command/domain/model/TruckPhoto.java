package com.barowoori.foodpinbackend.truck.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "truck_photos")
@Getter
public class TruckPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "path", length = 500)
    private String path;

    @Column(name = "updated_by")
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trucks_id")
    private Truck truck;

    protected TruckPhoto() {
    }

    @Builder
    public TruckPhoto(String path, String updatedBy, Truck truck) {
        this.path = path;
        this.updatedBy = updatedBy;
        this.truck = truck;
    }
}

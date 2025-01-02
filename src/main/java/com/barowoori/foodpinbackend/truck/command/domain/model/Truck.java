package com.barowoori.foodpinbackend.truck.command.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "trucks")
@Getter
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "name", nullable = false)
    private String name;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "electricity_usage")
    private Boolean electricityUsage;

    @Column(name = "gas_usage")
    private Boolean gasUsage;

    @Column(name = "self_generation_availability")
    private Boolean selfGenerationAvailability;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    protected Truck(){}

    public Truck(String name, LocalDateTime updatedAt, String updatedBy, String description, Boolean electricityUsage, Boolean gasUsage, Boolean selfGenerationAvailability, Boolean isDeleted) {
        this.name = name;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.description = description;
        this.electricityUsage = electricityUsage;
        this.gasUsage = gasUsage;
        this.selfGenerationAvailability = selfGenerationAvailability;
        this.isDeleted = isDeleted;
    }

}

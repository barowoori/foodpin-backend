package com.barowoori.foodpinbackend.truck.command.domain.model;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trucks")
@Getter
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

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

    @Column(name = "views")
    private Integer views = 0;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "truck")
    @BatchSize(size = 10)
    private List<TruckMenu> menus = new ArrayList<>();

    @OneToMany(mappedBy = "truck")
    @BatchSize(size = 10)
    private List<TruckPhoto> photos = new ArrayList<>();

    @OneToMany(mappedBy = "truck")
    private List<TruckDocument> documents = new ArrayList<>();

    protected Truck() {
    }

    @Builder
    public Truck(String name, LocalDateTime updatedAt, String updatedBy, String description, Boolean electricityUsage, Boolean gasUsage, Boolean selfGenerationAvailability, Boolean isDeleted, Integer views) {
        this.name = name;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.description = description;
        this.electricityUsage = electricityUsage;
        this.gasUsage = gasUsage;
        this.selfGenerationAvailability = selfGenerationAvailability;
        this.isDeleted = isDeleted;
        this.views = views;
    }

    public void update(String name, String updatedBy, String description, Boolean electricityUsage, Boolean gasUsage, Boolean selfGenerationAvailability) {
        this.name = name;
        this.updatedBy = updatedBy;
        this.description = description;
        this.electricityUsage = electricityUsage;
        this.gasUsage = gasUsage;
        this.selfGenerationAvailability = selfGenerationAvailability;
    }
  
    public void addViews() {
        this.views = this.views + 1;

    }

    public void delete(){
        this.isDeleted = true;
    }

    public Boolean approval(){
       return this.documents.stream().anyMatch(doc -> doc.getType().equals(DocumentType.BUSINESS_REGISTRATION));
    }
}

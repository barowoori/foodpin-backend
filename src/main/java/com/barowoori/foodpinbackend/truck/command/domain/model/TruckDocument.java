package com.barowoori.foodpinbackend.truck.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "truck_documents")
@Getter
public class TruckDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DocumentType type;

    @Column(name = "path", length = 500)
    private String path;

    @Column(name = "is_approval")
    private Boolean approval;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trucks_id")
    private Truck truck;

    protected TruckDocument() {
    }

    @Builder
    public TruckDocument(LocalDateTime updatedAt, String updatedBy, DocumentType type, String path, Boolean approval, Truck truck) {
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.type = type;
        this.path = path;
        this.approval = approval;
        this.truck = truck;
    }
}

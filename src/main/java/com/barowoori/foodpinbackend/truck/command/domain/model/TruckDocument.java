package com.barowoori.foodpinbackend.truck.command.domain.model;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "truck_documents")
@Getter
public class TruckDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DocumentType type;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "is_approval")
    private Boolean approval;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trucks_id")
    private Truck truck;

    @OneToMany(mappedBy = "truckDocument")
    private List<TruckDocumentPhoto> photos = new ArrayList<>();

    protected TruckDocument() {
    }

    @Builder
    public TruckDocument(LocalDateTime updatedAt, String updatedBy, DocumentType type, String documentId, Boolean approval, Truck truck) {
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.type = type;
        this.documentId = documentId;
        this.approval = approval;
        this.truck = truck;
    }
}

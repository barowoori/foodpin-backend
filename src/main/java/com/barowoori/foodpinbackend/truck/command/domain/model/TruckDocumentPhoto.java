package com.barowoori.foodpinbackend.truck.command.domain.model;

import com.barowoori.foodpinbackend.file.command.domain.model.File;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "truck_document_photos")
@Getter
public class TruckDocumentPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "files_id")
    private File file;

    @Column(name = "updated_by")
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_document_id")
    private TruckDocument truckDocument;

    @Builder
    public TruckDocumentPhoto(File file, String updatedBy, TruckDocument truckDocument) {
        this.file = file;
        this.updatedBy = updatedBy;
        this.truckDocument = truckDocument;
    }
}

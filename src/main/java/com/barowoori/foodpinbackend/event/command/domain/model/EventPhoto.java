package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.file.command.domain.model.File;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_photos")
@Getter
public class EventPhoto {
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
    @JoinColumn(name = "events_id")
    private Event event;

    protected EventPhoto() {
    }

    @Builder
    public EventPhoto(File file, String updatedBy, Event event) {
        this.file = file;
        this.updatedBy = updatedBy;
        this.event = event;
    }
}

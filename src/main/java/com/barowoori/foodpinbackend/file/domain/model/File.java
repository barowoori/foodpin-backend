package com.barowoori.foodpinbackend.file.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Getter
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "path", length = 500)
    private String path;

    protected File(){}

    @Builder
    public File(String path) {
        this.path = path;
    }
}

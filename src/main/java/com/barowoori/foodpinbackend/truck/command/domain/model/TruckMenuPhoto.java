package com.barowoori.foodpinbackend.truck.command.domain.model;

import com.barowoori.foodpinbackend.file.command.domain.model.File;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "truck_Menu_photos")
@Getter
public class TruckMenuPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "files_id")
    private File file;

    @Column(name = "updated_by")
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_menus_id")
    private TruckMenu truckMenu;

    protected TruckMenuPhoto() {
    }
    @Builder
    public TruckMenuPhoto(File file, String updatedBy, TruckMenu truckMenu) {
        this.file = file;
        this.updatedBy = updatedBy;
        this.truckMenu = truckMenu;
    }
}

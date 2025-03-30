package com.barowoori.foodpinbackend.truck.command.domain.model;

import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "truck_menus")
@Getter
public class TruckMenu {
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

    @Column(name = "price")
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trucks_id", nullable = false)
    private Truck truck;

    @OneToMany(mappedBy = "truckMenu")
    private List<TruckMenuPhoto> photos = new ArrayList<>();

    protected TruckMenu() {
    }

    @Builder
    public TruckMenu(String name, LocalDateTime updatedAt, String updatedBy, String description, Integer price, Truck truck) {
        this.name = name;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.description = description;
        this.price = price;
        this.truck = truck;
    }

    public String getTruckMenuMainPhotoUrl(ImageManager imageManager){
        return getTruckMenuPhotoFiles().stream()
                .map(file -> imageManager.getPreSignUrl(file.getPath()))
                .findFirst().orElse(null);
    }

    public List<File> getTruckMenuPhotoFiles(){
        return photos.stream()
                .sorted(Comparator.comparing(TruckMenuPhoto::getCreateAt).reversed())
                .map(TruckMenuPhoto::getFile)
                .toList();
    }
}

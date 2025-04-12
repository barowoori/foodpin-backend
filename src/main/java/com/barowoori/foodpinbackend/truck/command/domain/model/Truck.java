package com.barowoori.foodpinbackend.truck.command.domain.model;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.region.command.domain.query.application.RegionSearchProcessor;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
    private Set<TruckMenu> menus;

    @OneToMany(mappedBy = "truck")
    @BatchSize(size = 10)
    private Set<TruckPhoto> photos;

    @OneToMany(mappedBy = "truck")
    private Set<TruckDocument> documents;

    @OneToMany(mappedBy = "truck")
    private Set<TruckCategory> categories ;

    @OneToMany(mappedBy = "truck")
    private Set<TruckRegion> regions;

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
        if (this.documents == null){
            return Boolean.FALSE;
        }
       return this.documents.stream().anyMatch(doc -> doc.getType().equals(DocumentType.BUSINESS_REGISTRATION));
    }

    public String getTruckMainPhotoUrl(ImageManager imageManager){
        return getTruckPhotoFiles().stream()
                .map(file -> imageManager.getPreSignUrl(file.getPath()))
                .findFirst().orElse(null);
    }

    public List<File> getTruckPhotoFiles(){
        if (this.photos == null){
            return new ArrayList<>();
        }
        return this.photos.stream()
                .sorted(Comparator.comparing(TruckPhoto::getCreateAt))
                .map(TruckPhoto::getFile)
                .toList();
    }

    public List<TruckMenu> getSortedTruckMenus(){
        if(this.menus == null){
            return new ArrayList<>();
        }
        return this.menus.stream()
                .sorted(Comparator.comparing(TruckMenu::getCreateAt))
                .toList();
    }

    public List<String> getSortedTruckMenuNames(){
        return getSortedTruckMenus().stream().map(TruckMenu::getName).toList();
    }

    public List<TruckRegion> getSortedTruckRegions(){
        if(this.regions == null){
            return new ArrayList<>();
        }
        return this.regions.stream()
                .sorted(Comparator.comparing(TruckRegion::getCreateAt))
                .toList();
    }

    public List<String> getTruckRegionNames(RegionSearchProcessor regionSearchProcessor){
        return getSortedTruckRegions().stream()
                .map(truckRegion ->
                        regionSearchProcessor.findFullRegionName(truckRegion.getRegionType(), truckRegion.getRegionId()))
                .toList();
    }
}

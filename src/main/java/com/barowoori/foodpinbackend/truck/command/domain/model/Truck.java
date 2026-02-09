package com.barowoori.foodpinbackend.truck.command.domain.model;

import com.barowoori.foodpinbackend.document.command.domain.model.DocumentType;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.region.command.domain.query.application.RegionSearchProcessor;
import com.barowoori.foodpinbackend.truck.command.domain.service.*;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

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
    private Set<TruckCategory> categories;

    @OneToMany(mappedBy = "truck")
    private Set<TruckRegion> regions;

    @Column(name = "colors")
    @Convert(converter = TruckColorSetConverter.class)
    private Set<TruckColor> colors = new HashSet<>();

    @Column(name = "body_type")
    @Enumerated(value = EnumType.STRING)
    private TruckBodyType bodyType;

    @Column(name = "is_catering")
    private Boolean isCatering;

    @Column(name = "types")
    @Convert(converter = TruckTypeSetConverter.class)
    private Set<TruckType> types = new HashSet<>();

    @Column(name = "payment_methods")
    @Convert(converter = PaymentMethodSetConverter.class)
    private Set<PaymentMethod> paymentMethods = new HashSet<>();

    @Column(name = "proof_issuance_types")
    @Convert(converter = ProofIssuanceTypeSetConverter.class)
    private Set<ProofIssuanceType> proofIssuanceTypes = new HashSet<>();

    @Column(name = "avg_menu_price")
    private Integer avgMenuPrice;

    protected Truck() {
    }

    @Builder
    public Truck(String name, LocalDateTime updatedAt, String updatedBy, String description, Boolean electricityUsage, Boolean gasUsage, Boolean selfGenerationAvailability, Boolean isDeleted, Integer views,
                 Set<TruckColor> colors, TruckBodyType bodyType, Boolean isCatering, Set<TruckType> types, Set<PaymentMethod> paymentMethods, Set<ProofIssuanceType> proofIssuanceTypes) {
        this.name = name;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.description = description;
        this.electricityUsage = electricityUsage;
        this.gasUsage = gasUsage;
        this.selfGenerationAvailability = selfGenerationAvailability;
        this.isDeleted = isDeleted;
        this.views = views;
        this.colors = colors;
        this.bodyType = bodyType;
        this.isCatering = isCatering;
        this.types = types;
        this.paymentMethods = paymentMethods;
        this.proofIssuanceTypes = proofIssuanceTypes;
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
        this.name = "(삭제됨) " + this.name;
        this.isDeleted = true;
    }

    public void deleteByMember(){
        this.name = "(탈퇴) " + this.name;
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

    public List<String> getFirstTwoCreatedTruckMenuPhotos(ImageManager imageManager){
        if(this.menus == null){
            return new ArrayList<>();
        }
        return this.menus.stream()
                .sorted(Comparator.comparing(TruckMenu::getCreateAt))
                .map(menu -> menu.getTruckMenuMainPhotoUrl(imageManager))
                .filter(Objects::nonNull)
                .limit(2)
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

    public void updateAvgMenuPrice() {
        if (menus == null || menus.isEmpty()) {
            this.avgMenuPrice = null;
            return;
        }

        int sum = menus.stream()
                .map(TruckMenu::getPrice)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        int count = (int) menus.stream()
                .map(TruckMenu::getPrice)
                .filter(Objects::nonNull)
                .count();

        this.avgMenuPrice = count == 0 ? null : sum / count;
    }
}

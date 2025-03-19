package com.barowoori.foodpinbackend.document.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "business_registration")
public class BusinessRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "business_number")
    private String businessNumber;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "representative_name")
    private String representativeName;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    protected BusinessRegistration(){}

    @Builder
    public BusinessRegistration(String updatedBy, String businessNumber, String businessName, String representativeName, LocalDate openingDate) {
        this.updatedBy = updatedBy;
        this.businessNumber = businessNumber;
        this.businessName = businessName;
        this.representativeName = representativeName;
        this.openingDate = openingDate;
    }

    public void update(String updatedBy, String businessNumber, String businessName, String representativeName, LocalDate openingDate){
        this.updatedBy = updatedBy;
        this.businessNumber = businessNumber;
        this.businessName = businessName;
        this.representativeName = representativeName;
        this.openingDate = openingDate;
    }
}

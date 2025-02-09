package com.barowoori.foodpinbackend.document.command.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "business_registration")
public class BusinessRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "business_number")
    private String businessNumber;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "representative_name")
    private String representativeName;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    public BusinessRegistration(String businessNumber, String businessName, String representativeName, LocalDate openingDate) {
        this.businessNumber = businessNumber;
        this.businessName = businessName;
        this.representativeName = representativeName;
        this.openingDate = openingDate;
    }
}

package com.barowoori.foodpinbackend.region.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "sidos")
@Getter
public class Sido {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    protected Sido(){}

    @Builder
    public Sido(String name) {
        this.name = name;
    }
}

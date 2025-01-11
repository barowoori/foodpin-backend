package com.barowoori.foodpinbackend.region.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "sigungus")
@Getter
public class Sigungu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "sidos_id")
    private Sido sido;

    protected Sigungu(){}

    @Builder
    public Sigungu(String name, Sido sido) {
        this.name = name;
        this.sido = sido;
    }
}

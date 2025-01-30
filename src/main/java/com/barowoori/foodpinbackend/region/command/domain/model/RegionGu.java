package com.barowoori.foodpinbackend.region.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "region_gu")
@Getter
public class RegionGu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "region_do_id")
    private RegionDo regionDo;

    @ManyToOne
    @JoinColumn(name = "region_si_id")
    private RegionSi regionSi;

    protected RegionGu(){}

    @Builder
    public RegionGu(String name) {
        this.name = name;
    }
}

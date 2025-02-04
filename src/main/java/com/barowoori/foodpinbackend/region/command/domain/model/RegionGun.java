package com.barowoori.foodpinbackend.region.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "region_gun")
@Getter
public class RegionGun {
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

    @ManyToOne
    @JoinColumn(name = "region_gu_id")
    private RegionGu regionGu;

    protected RegionGun(){}

    @Builder
    public RegionGun(String name) {
        this.name = name;
    }
}

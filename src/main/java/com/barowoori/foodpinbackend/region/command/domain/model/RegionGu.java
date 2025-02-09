package com.barowoori.foodpinbackend.region.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "region_gu")
@Getter
public class RegionGu extends Region {
    @ManyToOne
    @JoinColumn(name = "region_do_id")
    private RegionDo regionDo;

    @ManyToOne
    @JoinColumn(name = "region_si_id")
    private RegionSi regionSi;

    public static class Builder extends Region.Builder<RegionGu.Builder> {
        private RegionDo regionDo;
        private RegionSi regionSi;

        public Builder() {
        }

        public Builder addRegionDo(RegionDo regionDo) {
            this.regionDo = regionDo;
            return this;
        }

        public Builder addRegionSi(RegionSi regionSi) {
            this.regionSi = regionSi;
            return this;
        }

        @Override
        protected RegionGu.Builder self() {
            return this;
        }

        @Override
        public RegionGu build() {
            return new RegionGu(this);
        }
    }

    public RegionGu(RegionGu.Builder builder) {
        super(builder);
        this.regionDo = builder.regionDo;
        this.regionSi = builder.regionSi;
    }
}

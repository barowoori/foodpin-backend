package com.barowoori.foodpinbackend.region.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "region_gun")
@Getter
public class RegionGun extends Region {
    @ManyToOne
    @JoinColumn(name = "region_do_id")
    private RegionDo regionDo;

    @ManyToOne
    @JoinColumn(name = "region_si_id")
    private RegionSi regionSi;

    @ManyToOne
    @JoinColumn(name = "region_gu_id")
    private RegionGu regionGu;

    public static class Builder extends Region.Builder<RegionGun.Builder> {
        private RegionDo regionDo;
        private RegionSi regionSi;
        private RegionGu regionGu;

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

        public Builder addRegionGu(RegionGu regionGu) {
            this.regionGu = regionGu;
            return this;
        }

        @Override
        protected RegionGun.Builder self() {
            return this;
        }

        @Override
        public RegionGun build() {
            return new RegionGun(this);
        }
    }

    protected RegionGun(){
        super();
    }

    public RegionGun(RegionGun.Builder builder) {
        super(builder);
        this.regionDo = builder.regionDo;
        this.regionSi = builder.regionSi;
        this.regionGu = builder.regionGu;
    }

}

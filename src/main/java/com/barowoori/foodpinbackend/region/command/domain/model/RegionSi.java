package com.barowoori.foodpinbackend.region.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "region_si")
@Getter
public class RegionSi extends Region{

    @ManyToOne
    @JoinColumn(name = "region_do_id")
    private RegionDo regionDo;

    public static class Builder extends Region.Builder<RegionSi.Builder> {
        private RegionDo regionDo;

        public Builder() {
        }

        public Builder addRegionDo(RegionDo regionDo){
            this.regionDo = regionDo;
            return this;
        }

        @Override
        protected RegionSi.Builder self() {
            return this;
        }

        @Override
        public RegionSi build() {
            return new RegionSi(this);
        }
    }

    public RegionSi (RegionSi.Builder builder) {
        super(builder);
        this.regionDo = builder.regionDo;
    }
}

package com.barowoori.foodpinbackend.region.command.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "region_do")
@Getter
public class RegionDo extends Region{

    public static class Builder extends Region.Builder<Builder> {

        public Builder() {

        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public RegionDo build() {
            return new RegionDo(this);
        }
    }

    public RegionDo (Builder builder) {
        super(builder);
    }
}

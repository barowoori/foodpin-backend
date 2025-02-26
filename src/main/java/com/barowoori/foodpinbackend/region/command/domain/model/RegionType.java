package com.barowoori.foodpinbackend.region.command.domain.model;

import lombok.Getter;

@Getter
public enum RegionType {
    REGION_DO("DO"), REGION_SI("SI"), REGION_GU("GU"), REGION_GUN("GUN");

    private final String code;

    RegionType(String code) {
        this.code = code;
    }

    public String makeCode(String id) {
        return this.getCode() + id;
    }
}

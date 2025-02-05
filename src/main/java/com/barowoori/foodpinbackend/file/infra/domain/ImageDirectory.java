package com.barowoori.foodpinbackend.file.infra.domain;

import lombok.Getter;

@Getter
public enum ImageDirectory {
    PROFILE("profile"),
    DEFAULT(""),
    TRUCK("truck"),
    TRUCK_MENU("truck_menu"),
    TRUCK_DOCUMENT("truck_document");

    private final String path;
    ImageDirectory(String path){
        this.path = path;
    }
}

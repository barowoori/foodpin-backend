package com.barowoori.foodpinbackend.member.infra.domain;

import lombok.Getter;

@Getter
public enum ImageDirectory {
    PROFILE("profile"), DEFAULT("");

    private final String path;
    ImageDirectory(String path){
        this.path = path;
    }
}

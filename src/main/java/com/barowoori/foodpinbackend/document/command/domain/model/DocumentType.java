package com.barowoori.foodpinbackend.document.command.domain.model;

import lombok.Getter;

@Getter
public enum DocumentType {
    BUSINESS_REGISTRATION("사업자등록증"),
    BUSINESS_LICENSE("영업신고증"),
    VEHICLE_REGISTRATION("자동차등록증"),
    SANITATION_EDUCATION("위생교육필증");

    private String name;

    DocumentType(String name){
        this.name = name;
    }
}

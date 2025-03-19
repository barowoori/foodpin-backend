package com.barowoori.foodpinbackend.document.command.domain.model;

import lombok.Getter;

@Getter
public enum DocumentType {
    BUSINESS_REGISTRATION("사업자등록증"),
    BUSINESS_LICENSE("영업신고증"),
    VEHICLE_REGISTRATION("자동차등록증"),
    SANITATION_EDUCATION("위생교육필증"),
    HEALTH_CERTIFICATE("보건증"),
    GAS_SAFETY_INSPECTION_CERTIFICATE("가스안전점검필증");

    private String name;

    DocumentType(String name){
        this.name = name;
    }
}

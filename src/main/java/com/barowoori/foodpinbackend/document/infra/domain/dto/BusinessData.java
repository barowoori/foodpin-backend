package com.barowoori.foodpinbackend.document.infra.domain.dto;

import lombok.Getter;

@Getter
public class BusinessData {
    private String b_no;
    private String valid;
    private String valid_msg;

    public BusinessData(String b_no, String valid, String valid_msg) {
        this.b_no = b_no;
        this.valid = valid;
        this.valid_msg = valid_msg;
    }
}

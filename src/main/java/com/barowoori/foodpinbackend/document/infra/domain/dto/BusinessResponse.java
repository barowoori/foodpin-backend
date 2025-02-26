package com.barowoori.foodpinbackend.document.infra.domain.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class BusinessResponse {
    private String status_code;
    private int request_cnt;
    private int valid_cnt;
    private List<BusinessData> data;

    public BusinessResponse(String status_code, int request_cnt, int valid_cnt, List<BusinessData> data) {
        this.status_code = status_code;
        this.request_cnt = request_cnt;
        this.valid_cnt = valid_cnt;
        this.data = data;
    }

    public Boolean isValid(){
        return data.getFirst().getValid().equals("01");
    }
}

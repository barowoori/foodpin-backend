package com.barowoori.foodpinbackend.document.infra.domain.dto;

import com.barowoori.foodpinbackend.common.exception.CommonErrorCode;
import com.barowoori.foodpinbackend.common.exception.CustomException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
@Getter
public class BusinessInfo {
    private String b_no;
    private String start_dt;
    private String p_nm;

    protected BusinessInfo(){}
    @Builder
    public BusinessInfo(String b_no, String p_nm, LocalDate start_dt) {
        validate(b_no, start_dt, p_nm);
        this.b_no = b_no;
        this.start_dt = start_dt.toString().replace("-", "");
        this.p_nm = p_nm;
    }

    private void validate(String b_no, LocalDate start_dt, String p_nm){
        if (b_no == null) {
            throw new CustomException(CommonErrorCode.EMPTY_PARAMETER, "b_no");
        }
        if (p_nm == null) {
            throw new CustomException(CommonErrorCode.EMPTY_PARAMETER, "p_nm");
        }
        if (start_dt == null) {
            throw new CustomException(CommonErrorCode.EMPTY_PARAMETER, "start_dt");
        }
    }
}

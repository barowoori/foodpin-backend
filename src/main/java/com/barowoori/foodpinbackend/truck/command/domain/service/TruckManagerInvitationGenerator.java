package com.barowoori.foodpinbackend.truck.command.domain.service;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import org.springframework.stereotype.Service;

@Service
public class TruckManagerInvitationGenerator {

    public String getMessage(Truck truck) {
        return getDefaultMessage(truck.getName())
                + "\n"
                + "\n■ 초대코드 : " + getCode(truck.getId());
//                + "\n■ 링크 : " + getLink();
    }

    public Boolean matchInvitationCode(Truck truck, String code) {
        return getCode(truck.getId()).equals(code);
    }

    private String getDefaultMessage(String truckName) {
        return truckName + " 운영자로 초대합니다!\n" +
                "아래 초대코드를 입력하면 운영자로 등록이 완료됩니다.";
    }

    private String getCode(String truckId) {
        return truckId.substring(truckId.length() - 6);
    }

    private String getLink() {
        return "link";
    }

}

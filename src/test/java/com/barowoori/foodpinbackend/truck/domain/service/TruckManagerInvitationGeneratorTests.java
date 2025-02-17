package com.barowoori.foodpinbackend.truck.domain.service;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import com.barowoori.foodpinbackend.truck.command.domain.service.TruckManagerInvitationGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TruckManagerInvitationGeneratorTests {
    @Autowired
    TruckManagerInvitationGenerator truckManagerInvitationGenerator;
    @Autowired
    private TruckRepository truckRepository;

    Truck truck;

    @BeforeEach
    void setUp() {
        truck = Truck.builder()
                .name("바로우리")
                .description("바로우리 트럭입니다")
                .isDeleted(Boolean.FALSE)
                .build();
        truck = truckRepository.save(truck);
    }

    @Test
    @DisplayName("초대메세지 문구 조회")
    void GetInvitationMessage(){
        String message = truckManagerInvitationGenerator.getMessage(truck);
        assertNotNull(message);
        System.out.println(message);
    }

    @Test
    @DisplayName("초대코드 일치할 때는 true를 반환한다")
    void WhenMatchCode_Return_True(){
       assertTrue(truckManagerInvitationGenerator.matchInvitationCode(truck, truck.getId().substring(truck.getId().length()-6)));
    }

    @Test
    @DisplayName("초대코드 일치하지 않을 때는 false를 반환한다")
    void WhenMatchCode_Return_False(){
        assertFalse(truckManagerInvitationGenerator.matchInvitationCode(truck, "123456"));
    }
}

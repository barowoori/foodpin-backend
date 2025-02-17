package com.barowoori.foodpinbackend.document.domain.infra.domain;

import com.barowoori.foodpinbackend.document.infra.domain.OdCloudBusinessNumberValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
public class OdCloudBusinessNumberValidatorTests {
    @Autowired
    private OdCloudBusinessNumberValidator validator;

    @Test
    @DisplayName("사업자번호 진위 여부 조회")
    void validate(){
       assertFalse(validator.validate("111111111","테스터", LocalDate.now()));
    }
}

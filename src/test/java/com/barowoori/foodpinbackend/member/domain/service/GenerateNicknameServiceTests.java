package com.barowoori.foodpinbackend.member.domain.service;

import com.barowoori.foodpinbackend.member.command.domain.service.GenerateNicknameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class GenerateNicknameServiceTests {
    @Autowired
    private GenerateNicknameService generateNicknameService;

    @Test
    @DisplayName("랜덤 생성한 닉네임 뒤에 4자리 랜덤 숫자가 붙어야 한다")
    void GenerateRandomNumber() {
        String nickname = generateNicknameService.generationNickname();
        int number = Integer.parseInt(nickname.substring(nickname.length() - 4));
        assertTrue(number >= 1000 && number <= 9999, "숫자는 4자리 숫자이어야 한다");
    }
}

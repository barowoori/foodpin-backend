package com.barowoori.foodpinbackend.member.command.domain.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class GenerateNicknameService {
    private final List<String> firstLists = List.of("멋진", "빠른", "행복한", "용감한", "웃는");
    private final List<String> secondLists = List.of("호랑이", "독수리", "고양이", "사자", "코끼리");
    private final Random random = new Random();

    public String generationNickname() {
        String first = firstLists.get(random.nextInt(firstLists.size()));
        String second = secondLists.get(random.nextInt(secondLists.size()));
        int randomNumber = 1000 + random.nextInt(9000);
        return first + " " + second + "#" + randomNumber;
    }
}

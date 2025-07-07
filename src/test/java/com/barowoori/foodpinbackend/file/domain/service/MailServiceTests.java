package com.barowoori.foodpinbackend.file.domain.service;

import com.barowoori.foodpinbackend.file.command.domain.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class MailServiceTests {
    @Autowired
    private MailService mailService;

//    @Test
//    void sendMail(){
//        mailService.sendMailWithAttachments("", "test", "테스트내용", null);
//    }
}

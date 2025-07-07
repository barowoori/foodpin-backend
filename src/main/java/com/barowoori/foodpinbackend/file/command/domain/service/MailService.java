package com.barowoori.foodpinbackend.file.command.domain.service;


import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.file.command.domain.FileErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class MailService {
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMailWithAttachments(String to, String subject, String text, List<File> attachments) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);
            if (attachments != null && !attachments.isEmpty()) {
                for (File file : attachments) {
                    helper.addAttachment(file.getName(), new FileSystemResource(file));
                }
            }
            mailSender.send(message);
        } catch (MessagingException e) {
            log.info(e.getMessage());
            throw new CustomException(FileErrorCode.MAIL_SEND_FAILED);
        }
    }
}

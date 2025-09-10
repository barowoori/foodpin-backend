package com.barowoori.foodpinbackend.file.command.domain.service;


import com.amazonaws.services.ec2.model.IdFormat;
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
        sendMailWithAttachmentsInternal(to, subject, text, attachments, "");
    }

    public void sendTruckDocumentMailWithAttachments(String truckName, String to, String subject, String text, List<File> attachments) {
        if (truckName == null) {
            sendMailWithAttachmentsInternal(to, subject, text, attachments, "");
        } else {
            sendMailWithAttachmentsInternal(to, subject, text, attachments, truckName + "_");
        }
    }

    private void sendMailWithAttachmentsInternal(String to, String subject, String text, List<File> attachments, String fileNamePrefix) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom("barowoori19@gmail.com", "푸드핀");
            helper.setCc("barowoori19@gmail.com");
            helper.setSubject(subject);
            helper.setText(text, false);

            if (attachments != null && !attachments.isEmpty()) {
                for (File file : attachments) {
                    if (file != null){
                        helper.addAttachment(fileNamePrefix + file.getName(), new FileSystemResource(file));
                    }
                }
            }

            mailSender.send(message);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new CustomException(FileErrorCode.MAIL_SEND_FAILED);
        }
    }
}

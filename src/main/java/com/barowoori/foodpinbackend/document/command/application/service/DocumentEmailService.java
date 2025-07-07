package com.barowoori.foodpinbackend.document.command.application.service;

import com.barowoori.foodpinbackend.document.command.application.service.emailEvent.EventAppliedTruckDocumentSubmissionEvent;
import com.barowoori.foodpinbackend.document.command.application.service.emailEvent.EventSelectedTruckDocumentSubmissionEvent;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventDocumentSubmissionTarget;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.file.command.domain.service.MailService;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocument;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckDocumentPhoto;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class DocumentEmailService {
    private final MailService mailService;
    private final ImageManager imageManager;
    private final TruckDocumentRepository truckDocumentRepository;

    public DocumentEmailService(MailService mailService, ImageManager imageManager, TruckDocumentRepository truckDocumentRepository) {
        this.mailService = mailService;
        this.imageManager = imageManager;
        this.truckDocumentRepository = truckDocumentRepository;
    }

    @EventListener
    public void sendEventAppliedTruckDocuments(EventAppliedTruckDocumentSubmissionEvent eventAppliedTruckDocumentSubmissionEvent) {
        Event event = eventAppliedTruckDocumentSubmissionEvent.getEvent();
        Truck truck = eventAppliedTruckDocumentSubmissionEvent.getTruck();
        try {
            if (event.getDocumentSubmissionTarget().equals(EventDocumentSubmissionTarget.SELECTED_ONLY)) {
                return;
            }
            String to = event.getSubmissionEmail();
            String subject = "[푸드핀] 행사 지원자 서류 제출 임시 제목";
            String text = "임시 내용";

            List<File> attachments = truckDocumentRepository.getDocumentManager(truck.getId())
                    .getDocuments().stream()
                    .filter(truckDocument -> event.getEventDocumentTypes().contains(truckDocument.getType()))
                    .map(truckDocument ->
                            imageManager.downloadFile(getDocumentFileUrl(truckDocument)))
                    .toList();
            mailService.sendMailWithAttachments(to, subject, text, attachments);
        } catch (Exception e) {
            log.info("행사 지원 시 이메일 발송 실패 : " + e.getMessage());
        }
    }

    @EventListener
    public void sendEventSelectedTruckDocuments(EventSelectedTruckDocumentSubmissionEvent eventSelectedTruckDocumentSubmissionEvent) {
        Event event = eventSelectedTruckDocumentSubmissionEvent.getEvent();
        Truck truck = eventSelectedTruckDocumentSubmissionEvent.getTruck();
        try {
            if (event.getDocumentSubmissionTarget().equals(EventDocumentSubmissionTarget.ALL_APPLICANTS)) {
                return;
            }
            String to = event.getSubmissionEmail();
            String subject = "[푸드핀] 행사 선정된 푸드트럭 임시 제목";
            String text = "임시 내용";

            List<File> attachments = truckDocumentRepository.getDocumentManager(truck.getId())
                    .getDocuments().stream()
                    .filter(truckDocument -> event.getEventDocumentTypes().contains(truckDocument.getType()))
                    .map(truckDocument ->
                            imageManager.downloadFile(getDocumentFileUrl(truckDocument)))
                    .toList();
            mailService.sendMailWithAttachments(to, subject, text, attachments);

            log.info("행사 선정시 이메일 전송 성공 :" + to + " " + subject);
        } catch (Exception e) {
            log.info("행사 선정 시 이메일 발송 실패 : " + e.getMessage());
        }
    }

    private String getDocumentFileUrl(TruckDocument truckDocument) {
        return truckDocument.getPhotos().stream().findFirst()
                .map(TruckDocumentPhoto::getFile)
                .map(com.barowoori.foodpinbackend.file.command.domain.model.File::getPath)
                .orElse(null);
    }

}

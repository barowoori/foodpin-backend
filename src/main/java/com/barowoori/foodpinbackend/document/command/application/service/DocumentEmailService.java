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
import java.util.Objects;

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
            String subject = getSubject(event.getName(), truck.getName());
            String text = getText(event.getName(), truck.getName());


            List<File> attachments = truckDocumentRepository.getDocumentManager(truck.getId())
                    .getDocuments().stream()
                    .filter(truckDocument -> event.getEventDocumentTypes().contains(truckDocument.getType()))
                    .map(truckDocument ->
                            imageManager.downloadFile(getDocumentFileUrl(truckDocument)))
                    .filter(Objects::nonNull)
                    .toList();
            mailService.sendTruckDocumentMailWithAttachments(truck.getName(), to, subject, text, attachments);
            log.info("행사 지원 시 이메일 발송 성공 : eventId : {}, truckId : {} ", event.getId(), truck.getId());
        } catch (Exception e) {
            log.info("행사 지원 시 이메일 발송 실패 : eventId : {}, truckId : {} " + e.getMessage(), event.getId(), truck.getId());
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
            String subject = getSubject(event.getName(), truck.getName());
            String text = getText(event.getName(), truck.getName());

            List<File> attachments = truckDocumentRepository.getDocumentManager(truck.getId())
                    .getDocuments().stream()
                    .filter(truckDocument -> event.getEventDocumentTypes().contains(truckDocument.getType()))
                    .map(truckDocument ->
                            imageManager.downloadFile(getDocumentFileUrl(truckDocument)))
                    .filter(Objects::nonNull)
                    .toList();
            mailService.sendTruckDocumentMailWithAttachments(truck.getName(), to, subject, text, attachments);

            log.info("행사 선정 시 이메일 발송 성공 : eventId : {}, truckId : {} ", event.getId(), truck.getId());
        } catch (Exception e) {
            log.info("행사 선정 시 이메일 발송 실패 : eventId : {}, truckId : {} " + e.getMessage(), event.getId(), truck.getId());
        }
    }

    private String getDocumentFileUrl(TruckDocument truckDocument) {
        com.barowoori.foodpinbackend.file.command.domain.model.File file = truckDocument.getPhotos().stream().findFirst()
                .map(TruckDocumentPhoto::getFile).orElse(null);
        if (file == null){
            return null;
        }
        return file.getPath();
    }

    private String getSubject(String eventName, String truckName) {
        String subject = "[{행사명}] {푸드트럭명}님의 제출 서류입니다.";

        subject = subject.replace("{행사명}", eventName);
        subject = subject.replace("{푸드트럭명}", truckName);

        return subject;
    }

    private String getText(String eventName, String truckName) {

        String text = "안녕하세요. 푸드핀입니다.\n" +
                "\n" +
                "’{행사명}’에 지원한 {푸드트럭명}의 제출서류 전달드립니다.\n" +
                "\n" +
                "해당 서류는 행사 공고 등록 시 설정한 제출 조건에 따라 발송되었습니다.\n" +
                "자세한 지원 정보는 App에서 확인해 주세요.\n" +
                "\n" +
                "감사합니다.\n" +
                "\n" +
                "※ 본 메일은 발신 전용입니다.\n" +
                "문의사항은 App > 마이페이지 > 설정 > 고객센터를 이용해 주세요.";

        text = text.replace("{행사명}", eventName);
        text = text.replace("{푸드트럭명}", truckName);

        return text;
    }

}

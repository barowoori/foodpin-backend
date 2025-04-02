package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckPhoto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "guidelines", columnDefinition = "TEXT")
    private String guidelines;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "document_submission_target")
    private EventDocumentSubmissionTarget documentSubmissionTarget;

    @Column(name = "submission_email")
    private String submissionEmail;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private EventStatus status;

    @Column(name = "status_comment")
    private String statusComment;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToOne(mappedBy = "event")
    private EventView view;

    @OneToOne(mappedBy = "event")
    private EventRecruitDetail recruitDetail;

    @OneToOne(mappedBy = "event")
    private EventRegion eventRegion;

    @OneToMany(mappedBy = "event")
    private List<EventPhoto> photos = new ArrayList<>();


    @OneToMany(mappedBy = "event")
    private List<EventDate> eventDates = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<EventCategory> categories = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<EventDocument> documents = new ArrayList<>();

    protected Event() {
    }

    @Builder

    public Event(String createdBy, String name, String description, String guidelines, Boolean isDeleted,
                 EventDocumentSubmissionTarget documentSubmissionTarget, String submissionEmail, EventStatus status) {
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.guidelines = guidelines;
        this.isDeleted = isDeleted;
        this.documentSubmissionTarget = documentSubmissionTarget;
        this.submissionEmail = submissionEmail;
        this.status = status;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateGuidelines(String guidelines) {
        this.guidelines = guidelines;
    }

    public void updateSubmissionEmail(String submissionEmail){
        this.submissionEmail = submissionEmail;
    }

    public void updateDocumentSubmissionTarget(EventDocumentSubmissionTarget documentSubmissionTarget){
        this.documentSubmissionTarget = documentSubmissionTarget;
    }

    public void initEventRecruitDetail(EventRecruitDetail eventRecruitDetail){
        this.recruitDetail = eventRecruitDetail;
    }

    public void initEventView(EventView eventView){
        this.view = eventView;
    }

    public void initEventRegion(EventRegion eventRegion){
        this.eventRegion = eventRegion;
    }

    public Boolean isCreator(String memberId){
        return this.createdBy.equals(memberId);
    }

    public void delete(){
        this.isDeleted = true;
    }

    public void updateStatus(EventStatus eventStatus){
        this.status = eventStatus;
    }

    public String getEventMainPhotoUrl(ImageManager imageManager){
        return getEventPhotoFiles().stream()
                .map(file -> imageManager.getPreSignUrl(file.getPath()))
                .findFirst().orElse(null);
    }

    public List<File> getEventPhotoFiles(){
        return photos.stream()
                .sorted(Comparator.comparing(EventPhoto::getCreatedAt))
                .map(EventPhoto::getFile)
                .toList();
    }

    public List<EventDate> getSortedEventDates(){
        return eventDates.stream()
                .sorted(Comparator.comparing(EventDate::getCreatedAt))
                .toList();
    }
}

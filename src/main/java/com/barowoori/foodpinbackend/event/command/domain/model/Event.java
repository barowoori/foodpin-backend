package com.barowoori.foodpinbackend.event.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToOne(mappedBy = "event")
    private EventView view;

    @OneToOne(mappedBy = "event")
    private EventRecruitDetail recruitDetail;

    @OneToMany(mappedBy = "event")
    private List<EventPhoto> photos = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<EventRegion> regions = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<EventDate> eventDates = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<EventCategory> categories = new ArrayList<>();

    protected Event() {
    }

    @Builder

    public Event(String createdBy, String name, String description, String guidelines, Boolean isDeleted,
                 EventDocumentSubmissionTarget documentSubmissionTarget, String submissionEmail, EventStatus status, String statusComment) {
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.guidelines = guidelines;
        this.isDeleted = isDeleted;
        this.documentSubmissionTarget = documentSubmissionTarget;
        this.submissionEmail = submissionEmail;
        this.status = status;
        this.statusComment = statusComment;
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

    public void initEventRecruitDetail(EventRecruitDetail eventRecruitDetail){
        this.recruitDetail = eventRecruitDetail;
    }

    public void initEventView(EventView eventView){
        this.view = eventView;
    }
}

package com.barowoori.foodpinbackend.event.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_details")
@Getter
public class EventDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "generator_requirement")
    private Boolean generatorRequirement; //발전기 필요여부

    @Column(name = "electricity_support_availability") //전기 지원 여부
    private Boolean ElectricitySupportAvailability;

    @Column(name = "entry_fee")// 입점비
    private Integer entryFee;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "guidelines", columnDefinition = "TEXT")
    private String guidelines;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "document_submission_target")
    private EventDocumentSubmissionTarget documentSubmissionTarget;

    @Column(name = "submission_email")
    private String submissionEmail;

    @OneToOne
    @JoinColumn(name = "events_id")
    private Event event;

    protected EventDetail(){}

    @Builder
    public EventDetail(Boolean generatorRequirement, Boolean electricitySupportAvailability, Integer entryFee, String description, String guidelines, EventDocumentSubmissionTarget documentSubmissionTarget, String submissionEmail, Event event) {
        this.generatorRequirement = generatorRequirement;
        ElectricitySupportAvailability = electricitySupportAvailability;
        this.entryFee = entryFee;
        this.description = description;
        this.guidelines = guidelines;
        this.documentSubmissionTarget = documentSubmissionTarget;
        this.submissionEmail = submissionEmail;
        this.event = event;
    }
}

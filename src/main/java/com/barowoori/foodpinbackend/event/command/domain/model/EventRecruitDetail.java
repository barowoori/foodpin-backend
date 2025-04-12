package com.barowoori.foodpinbackend.event.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_recruit_details")
@Getter
public class EventRecruitDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "recruit_end_date_time")
    private LocalDateTime recruitEndDateTime;

    @Column(name = "recruit_count")
    private Integer recruitCount;

    @Column(name = "generator_requirement")
    private Boolean generatorRequirement; //발전기 필요여부

    @Column(name = "electricity_support_availability") //전기 지원 여부
    private Boolean electricitySupportAvailability;

    @Column(name = "entry_fee")// 입점비
    private Integer entryFee;

    @ColumnDefault("0")
    @Column(name = "applicant_count")
    private Integer applicantCount;

    @ColumnDefault("0")
    @Column(name = "selected_count")
    private Integer selectedCount;

    @OneToOne
    @JoinColumn(name = "events_id")
    private Event event;

    protected EventRecruitDetail(){}

    @Builder
    public EventRecruitDetail(LocalDateTime recruitEndDateTime, Integer recruitCount, Integer applicantCount, Integer selectedCount,
                              Boolean generatorRequirement, Boolean electricitySupportAvailability, Integer entryFee,
                              Event event) {
        this.recruitEndDateTime = recruitEndDateTime;
        this.recruitCount = recruitCount;
        this.selectedCount = selectedCount;
        this.applicantCount = applicantCount;
        this.event = event;
        this.generatorRequirement = generatorRequirement;
        this.electricitySupportAvailability = electricitySupportAvailability;
        this.entryFee = entryFee;
    }

    public void update(LocalDateTime recruitEndDateTime, Integer recruitCount,
                       Boolean generatorRequirement, Boolean electricitySupportAvailability, Integer entryFee){
        this.recruitEndDateTime = recruitEndDateTime;
        this.recruitCount = recruitCount;
        this.generatorRequirement = generatorRequirement;
        this.electricitySupportAvailability = electricitySupportAvailability;
        this.entryFee = entryFee;
    }
}

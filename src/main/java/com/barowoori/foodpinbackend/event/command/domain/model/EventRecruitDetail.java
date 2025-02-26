package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
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

    @Column(name = "recruit_start_date")
    private LocalDate recruitStartDate;

    @Column(name = "recruit_end_date")
    private LocalDate recruitEndDate;

    @Column(name = "recruit_count")
    private Integer recruitCount;

    @Column(name = "applicant_count")
    private Integer applicantCount;

    @Column(name = "selected_count")
    private Integer selectedCount;

    @OneToOne
    @JoinColumn(name = "events_id")
    private Event event;

    protected EventRecruitDetail(){}

    @Builder
    public EventRecruitDetail(LocalDate recruitStartDate, LocalDate recruitEndDate, Integer recruitCount, Integer applicantCount, Integer selectedCount, Event event) {
        this.recruitStartDate = recruitStartDate;
        this.recruitEndDate = recruitEndDate;
        this.recruitCount = recruitCount;
        this.applicantCount = applicantCount;
        this.selectedCount = selectedCount;
        this.event = event;
    }
}

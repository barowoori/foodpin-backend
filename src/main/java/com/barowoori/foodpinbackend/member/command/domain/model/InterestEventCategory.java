package com.barowoori.foodpinbackend.member.command.domain.model;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "interest_event_categories")
@Getter
public class InterestEventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "interest_events_id", nullable = false)
    private InterestEvent interestEvent;

    @ManyToOne
    @JoinColumn(name = "categories_id", nullable = false)
    private Category category;

    protected InterestEventCategory() {
    }

    @Builder
    public InterestEventCategory(InterestEvent interestEvent, Category category) {
        this.interestEvent = interestEvent;
        this.category = category;
    }
}

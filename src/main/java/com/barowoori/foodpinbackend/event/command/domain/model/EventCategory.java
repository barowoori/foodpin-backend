package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_categories")
@Getter
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "events_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "categories_id")
    private Category category;

    protected EventCategory(){
    }

    @Builder
    public EventCategory(Event event, Category category) {
        this.event = event;
        this.category = category;
    }
}

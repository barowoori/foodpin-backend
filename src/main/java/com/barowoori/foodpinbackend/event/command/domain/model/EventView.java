package com.barowoori.foodpinbackend.event.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "event_views")
@Getter
public class EventView {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ColumnDefault("0")
    @Column(name = "views")
    private Integer views;

    @OneToOne
    @JoinColumn(name = "events_id")
    private Event event;

    protected EventView() {
    }

    @Builder
    public EventView(Integer views, Event event) {
        this.views = views;
        this.event = event;
    }

    public void addViews() {
        this.views = this.views + 1;
    }
}

package com.barowoori.foodpinbackend.event.command.domain.model;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "event_notices")
@Getter
public class EventNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_id")
    private Event event;

    @OneToMany(mappedBy = "eventNotice", fetch = FetchType.LAZY)
    private List<EventNoticeView> views = new ArrayList<>();

    protected EventNotice() {
    }

    @Builder
    public EventNotice(String title, String content, Boolean isDeleted, Event event) {
        this.title = title;
        this.content = content;
        this.isDeleted = isDeleted;
        this.event = event;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void delete() {
        this.isDeleted = Boolean.TRUE;
    }

    public List<EventTruck> getReadEventTrucks() {
        return this.views.stream()
                .sorted(Comparator.comparing(EventNoticeView::getCreatedAt))
                .map(EventNoticeView::getEventTruck)
                .toList();
    }

    public List<String> getReadEventTruckNames() {
        return getReadEventTrucks().stream()
                .map(EventTruck::getTruck)
                .map(Truck::getName)
                .toList();
    }

    public List<EventTruck> getUnReadEventTrucks() {
        List<EventTruck> readEventTrucks = getReadEventTrucks();
        return this.event.getConfirmedEventTrucks()
                .stream()
                .filter(truck -> !readEventTrucks.contains(truck))
                .toList();
    }

    public List<String> getUnReadEventTruckNames() {
        return getUnReadEventTrucks().stream()
                .map(EventTruck::getTruck)
                .map(Truck::getName)
                .toList();
    }
}

package com.barowoori.foodpinbackend.member.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interest_events")
@Getter
public class InterestEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "members_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "interestEvent")
    private List<InterestEventRegion> regions = new ArrayList<>();

    @OneToMany(mappedBy = "interestEvent")
    private List<InterestEventCategory> categories = new ArrayList<>();

    protected InterestEvent() {
    }

    @Builder
    public InterestEvent(Member member) {
        this.member = member;
    }
}

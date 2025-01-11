package com.barowoori.foodpinbackend.member.command.domain.model;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "truck_likes")
@Getter
public class TruckLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "members_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "trucks_id", nullable = false)
    private Truck truck;

    protected TruckLike(){}

    @Builder
    public TruckLike(Member member, Truck truck) {
        this.member = member;
        this.truck = truck;
    }
}

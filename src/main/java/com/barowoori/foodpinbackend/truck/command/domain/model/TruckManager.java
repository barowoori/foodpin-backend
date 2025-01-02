package com.barowoori.foodpinbackend.truck.command.domain.model;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "truck_managers")
@Getter
public class TruckManager {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private TruckManagerRole role;

    @Column(name = "role_updated_at")
    private LocalDateTime roleUpdatedAt;

    @ManyToOne
    @JoinColumn(name = "members_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "trucks_id", nullable = false)
    private Truck truck;

    protected TruckManager() {
    }

    public TruckManager(TruckManagerRole role, LocalDateTime roleUpdatedAt, Member member, Truck truck) {
        this.role = role;
        this.roleUpdatedAt = roleUpdatedAt;
        this.member = member;
        this.truck = truck;
    }
}

package com.barowoori.foodpinbackend.truck.command.domain.model;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "truck_categories")
@Getter
public class TruckCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "trucks_id")
    private Truck truck;

    @ManyToOne
    @JoinColumn(name = "categories_id")
    private Category category;

    protected TruckCategory(){
    }

    @Builder
    public TruckCategory(Truck truck, Category category) {
        this.truck = truck;
        this.category = category;
    }
}

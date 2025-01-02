package com.barowoori.foodpinbackend.category.command.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "categories")
@Getter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    protected Category(){
    }

    public Category(String name) {
        this.name = name;
    }
}

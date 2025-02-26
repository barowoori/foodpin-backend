package com.barowoori.foodpinbackend.category.command.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "categories")
@Getter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String code;

    protected Category(){
    }

    @Builder
    public Category(String name, String code) {
        this.name = name;
        this.code = code;
    }
}

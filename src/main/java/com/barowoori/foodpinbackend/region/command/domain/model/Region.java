package com.barowoori.foodpinbackend.region.command.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@MappedSuperclass
public abstract class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    public Region(String name) {
        this.name = name;
    }

    public Region() {

    }

    abstract static class Builder<T extends Builder<T>> {
        private String name;

        public T addName(String name) {
            this.name = name;
            return self();
        }

        abstract Region build();
        protected abstract T self();


    }

     Region (Builder<?> builder) {
        name = builder.name;
    }
}

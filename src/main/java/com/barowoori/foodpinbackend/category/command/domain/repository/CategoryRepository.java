package com.barowoori.foodpinbackend.category.command.domain.repository;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}

package com.barowoori.foodpinbackend.category.command.domain.repository;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepositoryCustom;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}

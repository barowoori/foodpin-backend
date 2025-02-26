package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;

import java.util.List;

public interface TruckCategoryRepositoryCustom {
    List<Category> findCategoriesByTruckId(String truckId);
}

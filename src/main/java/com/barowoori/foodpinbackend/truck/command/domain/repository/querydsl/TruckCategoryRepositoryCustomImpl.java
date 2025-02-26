package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.category.command.domain.model.QCategory;
import com.barowoori.foodpinbackend.truck.command.domain.model.QTruckCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class TruckCategoryRepositoryCustomImpl implements TruckCategoryRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    public TruckCategoryRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Category> findCategoriesByTruckId(String truckId){
        QCategory category = QCategory.category;
        QTruckCategory truckCategory = QTruckCategory.truckCategory;
        return jpaQueryFactory.select(category)
                .from(truckCategory)
                .innerJoin(truckCategory.category, category)
                .where(truckCategory.truck.id.eq(truckId))
                .fetch();
    }
}

package com.barowoori.foodpinbackend.category;

import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryDataInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    private static final List<CategorySeed> CATEGORY_SEEDS = List.of(
            new CategorySeed("C02", "양식"),
            new CategorySeed("C03", "일식"),
            new CategorySeed("C04", "중식"),
            new CategorySeed("C05", "분식"),
            new CategorySeed("C06", "세계음식"),
            new CategorySeed("C07", "간식"),
            new CategorySeed("C08", "음료"),
            new CategorySeed("C09", "술"),
            new CategorySeed("C01", "한식"),
            new CategorySeed("C10", "기타")
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (CategorySeed categorySeed : CATEGORY_SEEDS) {
            if (categoryRepository.existsByCode(categorySeed.code())) {
                continue;
            }

            categoryRepository.save(Category.builder()
                    .code(categorySeed.code())
                    .name(categorySeed.name())
                    .build());
        }
    }

    private record CategorySeed(String code, String name) {
    }
}

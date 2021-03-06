package com.maxbys.page_with_tips_project.categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriesRepository extends JpaRepository<CategoryEntity, String> {
    List<CategoryEntity> findAllByOrderByName();
    Page<CategoryEntity> findAllBySuperiorCategoryIsNull(Pageable pageable);
    Page<CategoryEntity> findAllBySuperiorCategoryNameIs(String category, Pageable pageable);
}

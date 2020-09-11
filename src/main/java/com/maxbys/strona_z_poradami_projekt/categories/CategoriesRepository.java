package com.maxbys.strona_z_poradami_projekt.categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Category, String> {
    Page<Category> findAllByCategoryIsNull(Pageable pageable);
    Page<Category> findAllByCategoryNameIs(String category, Pageable pageable);
}

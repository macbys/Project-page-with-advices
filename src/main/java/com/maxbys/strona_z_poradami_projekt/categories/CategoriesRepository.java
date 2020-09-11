package com.maxbys.strona_z_poradami_projekt.categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriesRepository extends JpaRepository<Category, String> {
    public Page<Category> findAllByCategoryIsNull(Pageable pageable);
    // todo: zastanawiam się czy zamiast Category nie powinien być Long będący id
    public Page<Category> findAllByCategoryNameIs(String category, Pageable pageable);
}

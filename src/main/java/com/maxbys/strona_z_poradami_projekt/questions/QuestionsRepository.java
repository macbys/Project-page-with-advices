package com.maxbys.strona_z_poradami_projekt.questions;

import com.maxbys.strona_z_poradami_projekt.categories.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface QuestionsRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndCategoryName(Long id, String categoryName);
    Page<Question> findAllByCategoryNameIs(String category, Pageable pageable);
//    @Query("SELECT q FROM Question q WHERE q.user.email = :email")
    Page<Question> findAllByUserEmailIs(@Param("email") String email, Pageable pageable);
//    List<Question> findTop5ByOrderByCreationDateDesc();
    List<Question> findTop5ByOrderByIdDesc();
    @Query("SELECT q.category FROM Question q WHERE q.id = :id")
    Optional<Category> getCategoryFromQuestionById(@Param("id") Long id);
}

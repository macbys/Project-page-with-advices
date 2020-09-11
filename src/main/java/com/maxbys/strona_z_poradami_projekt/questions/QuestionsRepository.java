package com.maxbys.strona_z_poradami_projekt.questions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QuestionsRepository extends JpaRepository<Question, Long> {
    Page<Question> findAllByCategoryNameIs(String category, Pageable pageable);
    Page<Question> findAllByUserEmailIs(@Param("email") String email, Pageable pageable);
    List<Question> findTop5ByOrderByIdDesc();
}

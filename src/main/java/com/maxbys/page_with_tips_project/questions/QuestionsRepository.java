package com.maxbys.page_with_tips_project.questions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QuestionsRepository extends JpaRepository<QuestionEntity, Long> {
    Page<QuestionEntity> findAllByCategoryEntityNameIs(String category, Pageable pageable);
    Page<QuestionEntity> findAllByUserEntityEmailIs(@Param("email") String email, Pageable pageable);
    List<QuestionEntity> findTop5ByOrderByIdDesc();
}


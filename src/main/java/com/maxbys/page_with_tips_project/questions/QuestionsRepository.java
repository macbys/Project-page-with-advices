package com.maxbys.page_with_tips_project.questions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QuestionsRepository extends JpaRepository<QuestionEntity, Long> {
    Page<QuestionEntity> findAllByCategoryEntityNameIs(String category, Pageable pageable);
    Page<QuestionEntity> findAllByUserEntityEmailIs(@Param("email") String email, Pageable pageable);
    List<QuestionEntity> findTop5ByOrderByIdDesc();
    @Query("select q from QuestionEntity q where upper(q.value) like concat(concat('%',upper(?1)),'%') order by case when upper(q.value) like  concat(upper(?1),'%') THEN 1 else 2 end")
    Page<QuestionEntity> findQuestionsContainingString(String value, Pageable pageable);
}


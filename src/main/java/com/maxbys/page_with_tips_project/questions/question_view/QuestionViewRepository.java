package com.maxbys.page_with_tips_project.questions.question_view;

import com.maxbys.page_with_tips_project.questions.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;

public interface QuestionViewRepository extends JpaRepository<QuestionView, Long> {

    @Query("select qv.question from QuestionView qv inner join qv.question q where qv.creationTime=current_date group by q.id " +
            " order by count(qv.question) desc")
    Page<QuestionEntity> getMostPopularQuestionsToday(Pageable pageable);
    @Query("select qv.question from QuestionView qv inner join qv.question q where qv.creationTime >= :fromTheDay  group by q.id " +
            " order by count(qv.question) desc")
    Page<QuestionEntity> getMostPopularQuestionsFromTheDay(@Param("fromTheDay")Date fromTheDay, Pageable pageable);
}

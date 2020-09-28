package com.maxbys.page_with_tips_project.answers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswersRepository extends JpaRepository<AnswerEntity, Long> {

    @Query("select a from AnswerEntity a where a.questionEntity.id = :id")
    Page<AnswerEntity> findAllByQuestionEntityId(@Param("id") Long questionEntityId, Pageable pageable);
    @Query("select a from AnswerEntity a where a.userEntity.email = :email")
    Page<AnswerEntity> findAllByUserEntityEmail(@Param("email") String email, Pageable pageable);
    Page<AnswerEntity> findAllByUserEntityIdIs(Long userId, Pageable pageable);
}

package com.maxbys.page_with_tips_project.comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CommentsRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findAllByAnswerEntityId(Long id, Pageable pageable);
    Page<CommentEntity> findAllByUserEntity_Email(@Param("email") String email, Pageable pageable);
}

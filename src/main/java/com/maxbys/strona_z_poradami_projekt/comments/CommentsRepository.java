package com.maxbys.strona_z_poradami_projekt.comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByAnswerId(Long id, Pageable pageable);
    List<Comment> findAllByAnswerId(Long id);
    @Query("select c, concat('categories/',c.answer.question.category.name,'/questions/',c.answer.question.id) from Comment c where c.user.email = :email")
    Page<Object[]> findAllWithLinksByUserEmailIs(@Param("email") String email, Pageable pageable);
}

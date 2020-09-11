package com.maxbys.strona_z_poradami_projekt.comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CommentsRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByAnswerId(Long id, Pageable pageable);
    Page<Comment> findAllByUser_Email(@Param("email") String email, Pageable pageable);
}

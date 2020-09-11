package com.maxbys.strona_z_poradami_projekt.answers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface AnswersRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByIdAndQuestionId(Long id, Long questionId);
    Page<Answer> findAllByQuestionId(Long id, Pageable pageable);
    Page<Answer> findAllByUser_Email(@Param("email") String email, Pageable pageable);
}

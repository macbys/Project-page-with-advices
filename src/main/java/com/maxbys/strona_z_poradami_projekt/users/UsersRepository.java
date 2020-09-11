package com.maxbys.strona_z_poradami_projekt.users;


import com.maxbys.strona_z_poradami_projekt.answers.Answer;
import com.maxbys.strona_z_poradami_projekt.comments.Comment;
import com.maxbys.strona_z_poradami_projekt.questions.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;


public interface UsersRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);
//    @Query(value = "SELECT u.answers FROM User u WHERE u.name = :name")
//    Set<Answer> getAnswersForUserByName(@Param("name") String name);
//    @Query(value = "SELECT u.comments FROM User u WHERE u.name = :name")
//    Set<Comment> getCommentsForUserByName(@Param("name") String name);
//    @Query(value = "SELECT u.questions FROM User u WHERE u.name = :name")
//    Set<Question> getQuestionsForUserByName(@Param("name") String name);
}

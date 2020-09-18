//package com.maxbys.strona_z_poradami_projekt.questions.question_view;
//
//import com.maxbys.strona_z_poradami_projekt.questions.QuestionDTO;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.Date;
//
//public interface QuestionViewRepository extends JpaRepository<QuestionView, Long> {
//
//
//    @Query("select qv.question from QuestionView qv where qv.creationTime=current_date group by qv.question " +
//            " order by count(qv.question) desc")
//    Page<QuestionDTO> getMostPopularQuestionsToday(Pageable pageable);
//    @Query("select qv.question from QuestionView qv where qv.creationTime >= :fromTheDay  group by qv.question " +
//            " order by count(qv.question) desc")
//    Page<QuestionDTO> getMostPopularQuestionsFromTheDay(@Param("fromTheDay")Date fromTheDay, Pageable pageable);
//}

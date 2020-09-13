//package com.maxbys.strona_z_poradami_projekt.questions.question_view;
//
//import com.maxbys.strona_z_poradami_projekt.questions.QuestionDTO;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//@Service
//public class QuestionViewService {
//
//    private final QuestionViewRepository questionViewRepository;
//
//    public QuestionViewService(QuestionViewRepository questionViewRepository) {
//        this.questionViewRepository = questionViewRepository;
//    }
//
//    public void save(QuestionView questionView) {
//        questionViewRepository.save(questionView);
//    }
//
//    public Page<QuestionDTO> getMostPopularQuestionsToday(Pageable pageable) {
//        return questionViewRepository.getMostPopularQuestionsToday(pageable);
//    }
//
//
//}

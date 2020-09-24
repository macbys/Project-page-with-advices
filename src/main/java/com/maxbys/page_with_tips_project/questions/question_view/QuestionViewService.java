package com.maxbys.page_with_tips_project.questions.question_view;

import com.maxbys.page_with_tips_project.questions.QuestionDTO;
import com.maxbys.page_with_tips_project.questions.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionViewService {

    private final QuestionViewRepository questionViewRepository;

    public QuestionViewService(QuestionViewRepository questionViewRepository) {
        this.questionViewRepository = questionViewRepository;
    }

    public void save(QuestionView questionView) {
        questionViewRepository.save(questionView);
    }

    public Page<QuestionDTO> getMostPopularQuestionsToday() {
        Page<QuestionEntity> questionEntityPage = questionViewRepository.getMostPopularQuestionsToday(PageRequest.of(0, 5));
        return getQuestionDTOS(questionEntityPage);
    }

    private Page<QuestionDTO> getQuestionDTOS(Page<QuestionEntity> questionEntityPage) {
        List<QuestionEntity> questionEntityList = questionEntityPage.getContent();
        List<QuestionDTO> questionDTOList = questionEntityList.stream()
                .map(QuestionDTO::apply)
                .collect(Collectors.toList());
        PageImpl<QuestionDTO> questionDTOPage = new PageImpl<>(questionDTOList, PageRequest.of(0, 5), questionDTOList.size());
        return questionDTOPage;
    }

    public Page<QuestionDTO> getMostPopularQuestionsInSevenDays() {
        LocalDate todayMinusSixDaysLocalDate = LocalDate.now().minusDays(6);
        Date todayMinusSixDays = Date.valueOf(todayMinusSixDaysLocalDate);
        Page<QuestionEntity> questionEntityPage = questionViewRepository.getMostPopularQuestionsFromTheDay(todayMinusSixDays, PageRequest.of(0, 5));
        return getQuestionDTOS(questionEntityPage);
    }

    public Page<QuestionDTO> getMostPopularQuestionsInThirtyDays() {
        LocalDate todayMinusTwentyNineDaysLocalDate = LocalDate.now().minusDays(29);
        Date todayMinusTwentyNineDays = Date.valueOf(todayMinusTwentyNineDaysLocalDate);
        Page<QuestionEntity> questionEntityPage = questionViewRepository.getMostPopularQuestionsFromTheDay(todayMinusTwentyNineDays, PageRequest.of(0, 5));
        return getQuestionDTOS(questionEntityPage);
    }
}

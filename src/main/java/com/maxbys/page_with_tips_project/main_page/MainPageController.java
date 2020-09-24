package com.maxbys.page_with_tips_project.main_page;

import com.maxbys.page_with_tips_project.questions.QuestionDTO;
import com.maxbys.page_with_tips_project.questions.QuestionsService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class MainPageController {

    private final QuestionsService questionsService;

    public MainPageController(QuestionsService questionsService) {
        this.questionsService = questionsService;
    }

    @GetMapping("/")
    public String goToMainPage(Model model) {
        List<QuestionDTO> questionDTOS = questionsService.findTop5ByOrderByIdDesc();
        model.addAttribute("newestQuestions", questionDTOS);
        Page<QuestionDTO> mostPopularQuestionsToday = questionsService.getMostPopularQuestionsToday();
        model.addAttribute("mostPopularQuestionsToday", mostPopularQuestionsToday);
        Page<QuestionDTO> mostPopularQuestionsInSevenDays = questionsService.getMostPopularQuestionsInSevenDays();
        model.addAttribute("mostPopularQuestionsInSevenDays", mostPopularQuestionsInSevenDays);
        Page<QuestionDTO> mostPopularQuestionsInThirtyDays = questionsService.getMostPopularQuestionsInThirtyDays();
        model.addAttribute("mostPopularQuestionsInThirtyDays", mostPopularQuestionsInThirtyDays);
        return "home";
    }
}

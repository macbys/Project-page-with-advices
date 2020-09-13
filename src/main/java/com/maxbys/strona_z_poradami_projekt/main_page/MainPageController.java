package com.maxbys.strona_z_poradami_projekt.main_page;

import com.maxbys.strona_z_poradami_projekt.questions.QuestionDTO;
import com.maxbys.strona_z_poradami_projekt.questions.QuestionsService;
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
        return "home";
    }
}

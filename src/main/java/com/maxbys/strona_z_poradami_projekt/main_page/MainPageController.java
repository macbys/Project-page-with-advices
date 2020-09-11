package com.maxbys.strona_z_poradami_projekt.main_page;

import com.maxbys.strona_z_poradami_projekt.categories.Category;
import com.maxbys.strona_z_poradami_projekt.questions.Question;
import com.maxbys.strona_z_poradami_projekt.questions.QuestionsService;
import com.maxbys.strona_z_poradami_projekt.users.User;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.naming.Context;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class MainPageController {

    private final QuestionsService questionsService;

    public MainPageController(QuestionsService questionsService) {
        this.questionsService = questionsService;
    }

    @GetMapping("/")
    public String goToMainPage(Model model) {
        List<Question> questions = questionsService.findTop5ByOrderByIdDesc();
        model.addAttribute("newestQuestions", questions);
        return "home";
    }

}

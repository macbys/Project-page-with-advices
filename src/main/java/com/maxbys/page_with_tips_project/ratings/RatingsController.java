package com.maxbys.page_with_tips_project.ratings;

import com.maxbys.page_with_tips_project.answers.AnswerDTO;
import com.maxbys.page_with_tips_project.answers.AnswersService;
import com.maxbys.page_with_tips_project.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class RatingsController {

    private final AnswersService answersService;
    private final UsersService usersService;
    private final RatingsService ratingsService;

    @Autowired
    public RatingsController(AnswersService answersService, UsersService usersService, RatingsService ratingsService) {
        this.answersService = answersService;
        this.usersService = usersService;
        this.ratingsService = ratingsService;
    }

    @PostMapping("/answer/{answerId}/rateUp")
    public RedirectView rateAnswerUp(Principal principal, HttpServletRequest httpServletRequest, @PathVariable Long answerId) {
        String email = principal.getName();
        ratingsService.addRateUp(email, answerId);
        return getRedirectViewToQuestionPage(httpServletRequest, answerId);
    }

    private RedirectView getRedirectViewToQuestionPage(HttpServletRequest httpServletRequest, @PathVariable Long answerId) {
        AnswerDTO answerDTO = answersService.findById(answerId);
        Long questionId = answerDTO.getQuestionDTO().getId();
        return new RedirectView("/question/" + questionId + "?" + httpServletRequest.getQueryString());
    }

    @PostMapping("/answer/{answerId}/rateDown")
    public RedirectView rateAnswerDown(Principal principal, HttpServletRequest httpServletRequest, @PathVariable Long answerId) {
        String email = principal.getName();
        ratingsService.addRateDown(email, answerId);
        return getRedirectViewToQuestionPage(httpServletRequest, answerId);
    }
}

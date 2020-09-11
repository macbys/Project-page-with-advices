package com.maxbys.strona_z_poradami_projekt.answers;

import com.maxbys.strona_z_poradami_projekt.paginagtion.PaginationGenerator;
import com.maxbys.strona_z_poradami_projekt.questions.Question;
import com.maxbys.strona_z_poradami_projekt.questions.QuestionsService;
import com.maxbys.strona_z_poradami_projekt.users.User;
import com.maxbys.strona_z_poradami_projekt.users.UsersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class AnswersController {

    private final UsersService usersService;
    private final QuestionsService questionsService;
    private final AnswersService answersService;

    public AnswersController(UsersService usersService, QuestionsService questionsService, AnswersService answersService) {
        this.usersService = usersService;
        this.questionsService = questionsService;
        this.answersService = answersService;
    }

    @PostMapping("/categories/{categoryName}/questions/{questionId}")
    public RedirectView addAnswer(RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, @ModelAttribute Answer answerForm,
                                  @PathVariable String categoryName, @PathVariable Long questionId, Principal principal) {
        User user = getUser(principal);
        Question question = getQuestion(categoryName, questionId);
        if(answerForm.getValue().trim().length() < 8) {
            return getRedirectViewToQuestionPageWithErrorMessage(redirectAttributes, httpServletRequest);
        }
        saveAnswerInRepository(answerForm, user, question);
        return new RedirectView(httpServletRequest.getRequestURI() + "?page=0&size=5");
    }

    private User getUser(Principal principal) {
        String principalName = principal.getName();
        Optional<User> userOptional = usersService.findByEmail(principalName);
        return userOptional.orElseThrow(() -> new RuntimeException("User with email " + principalName + "doesn't exist"));
    }

    private Question getQuestion(String categoryName, Long questionId) {
        Optional<Question> questionOptional = questionsService.findByIdAndCategoryName(questionId, categoryName);
        return questionOptional.orElseThrow(() -> new RuntimeException("There is no question with" + questionId + " id with " + categoryName + " category name"));
    }

    private RedirectView getRedirectViewToQuestionPageWithErrorMessage(RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
        redirectAttributes.addFlashAttribute("errorMsg", "Answer must be at least 8 characters long");
        return new RedirectView(httpServletRequest.getRequestURI() + "?" + httpServletRequest.getQueryString());
    }

    private void saveAnswerInRepository(Answer answerForm, User user, Question question) {
        Answer answer = Answer.builder()
                .rating(0)
                .user(user)
                .question(question)
                .value(answerForm.getValue())
                .build();
        answersService.save(answer);
    }

    @PostMapping("/categories/{categoryName}/questions/{questionId}/answers/{answerId}/delete")
    public RedirectView deleteQuestion(Principal principal, @PathVariable String categoryName, @PathVariable Long questionId, @PathVariable Long answerId) {
        getQuestion(categoryName, questionId);
        Answer answer = getAnswer(answerId);
        if(answer.getUser().getEmail().equals(principal.getName())){
            answersService.deleteById(answerId);
            return new RedirectView("/");
        }
        throw new RuntimeException("User with email " + principal.getName() + " isn't allowed to delete answer with " + answerId + " id");
    }

    private Answer getAnswer(Long answerId) {
        Optional<Answer> answerOptional = answersService.findById(answerId);
        return answerOptional.orElseThrow(() -> new RuntimeException("Answer with" + answerId + " doesn't exist"));
    }

    @GetMapping("/profile/answers")
    String seeAllAnswersOfLoggedUser(Model model, Principal principal, Pageable pageable) {
        String userEmail = principal.getName();
        Page<Object[]> answersWithLinks = answersService.findAllWithLinksByUserEmailIs(userEmail, pageable);
        model.addAttribute("answersWithLinks", answersWithLinks);
        List<Integer> paginationNumbers;
        paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), answersWithLinks.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "answers-of-user";
    }


}

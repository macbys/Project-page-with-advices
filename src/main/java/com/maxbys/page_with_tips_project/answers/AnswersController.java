package com.maxbys.page_with_tips_project.answers;

import com.maxbys.page_with_tips_project.paginagtion.PaginationGenerator;
import com.maxbys.page_with_tips_project.questions.QuestionDTO;
import com.maxbys.page_with_tips_project.questions.QuestionsService;
import com.maxbys.page_with_tips_project.users.UserDTO;
import com.maxbys.page_with_tips_project.users.UsersService;
import org.dom4j.rule.Mode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @PostMapping("/question/{questionId}/answers")
    public RedirectView addAnswer(RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, @ModelAttribute AnswerDTO answerDTOForm
                                  , @PathVariable Long questionId, Principal principal) {
        UserDTO userDTO = getUser(principal);
        QuestionDTO questionDTO = getQuestion(questionId);
        if(answerDTOForm.getValue().length() < 8 || answerDTOForm.getValue().length() > 1800) {
            redirectAttributes.addFlashAttribute("errorMsg", "Answer must be between 8 and 1800 characters long");
            return new RedirectView("/question/" + questionId + "?" + httpServletRequest.getQueryString());
        }
        saveAnswerInRepository(answerDTOForm, userDTO, questionDTO);
        return new RedirectView("/question/" + questionId + "?" + httpServletRequest.getQueryString());
    }

    private UserDTO getUser(Principal principal) {
        String principalName = principal.getName();
        return usersService.findByEmail(principalName);
    }

    private QuestionDTO getQuestion(Long questionId) {
        return questionsService.findById(questionId);
    }

    private void saveAnswerInRepository(AnswerDTO answerDTOForm, UserDTO userDTO, QuestionDTO questionDTO) {
        AnswerDTO answerDTO = AnswerDTO.builder()
                .rating(0)
                .userDTO(userDTO)
                .questionDTO(questionDTO)
                .value(answerDTOForm.getValue())
                .build();
        answersService.save(answerDTO);
    }

    @PostMapping("/answer/{answerId}/delete")
    public RedirectView deleteAnswer(Principal principal,  @PathVariable Long answerId) {
        AnswerDTO answerDTO = getAnswer(answerId);
        if(answerDTO.getUserDTO().getEmail().equals(principal.getName())){
            answersService.deleteById(answerId);
            return new RedirectView("/");
        }
        throw new RuntimeException("User with email " + principal.getName() + " isn't allowed to delete answer with " + answerId + " id");
    }

    private AnswerDTO getAnswer(Long answerId) {
        return answersService.findById(answerId);
    }

    @GetMapping("/profile/answers")
    public String seeAllAnswersOfLoggedUser(Model model, Principal principal, Pageable pageable) {
        String userEmail = principal.getName();
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<AnswerDTO> answersWithLinks = answersService.findAllByUserEmail(userEmail, pageRequest);
        model.addAttribute("answers", answersWithLinks);
        List<Integer> paginationNumbers;
        paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), answersWithLinks.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "answers-of-logged-user";
    }

    @GetMapping("/user/{userId}/answers")
    public String seeAllAnswersOfUser(Model model, Pageable pageable, @PathVariable Long userId) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<AnswerDTO> answersWithLinks = answersService.findAllByUserEntityIdIs(userId, pageRequest);
        model.addAttribute("answers", answersWithLinks);
        UserDTO userDTO = usersService.findById(userId);
        model.addAttribute("user", userDTO);
        List<Integer> paginationNumbers;
        paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), answersWithLinks.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "answers-of-user";
    }

}

package com.maxbys.strona_z_poradami_projekt.questions;

import com.maxbys.strona_z_poradami_projekt.answers.AnswerDTO;
import com.maxbys.strona_z_poradami_projekt.answers.AnswersService;
import com.maxbys.strona_z_poradami_projekt.categories.CategoriesService;
import com.maxbys.strona_z_poradami_projekt.categories.CategoryDTO;
import com.maxbys.strona_z_poradami_projekt.comments.CommentDTO;
import com.maxbys.strona_z_poradami_projekt.comments.CommentsService;
import com.maxbys.strona_z_poradami_projekt.paginagtion.PaginationGenerator;
import com.maxbys.strona_z_poradami_projekt.users.UserDTO;
import com.maxbys.strona_z_poradami_projekt.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class QuestionsController {

    private final QuestionsService questionsService;
    private final CategoriesService categoriesService;
    private final UsersService usersService;
    private final AnswersService answersService;
    private final CommentsService commentsService;

    @Autowired
    public QuestionsController(QuestionsService questionsService, CategoriesService categoriesService, UsersService usersService, AnswersService answersService, CommentsService commentsService) {
        this.questionsService = questionsService;
        this.categoriesService = categoriesService;
        this.usersService = usersService;
        this.answersService = answersService;
        this.commentsService = commentsService;
    }

    @GetMapping("/add-question")
    public String goToAddQuestionPage(Model model) {
        List<CategoryDTO> categories = categoriesService.findAllByOrOrderByName();
        model.addAttribute("categories", categories);
        model.addAttribute("question", new FormQuestionTemplate());
        return "add-question";
    }

    @PostMapping("/add-question")
    public RedirectView postQuestion(RedirectAttributes redirectAttributes, @ModelAttribute FormQuestionTemplate formQuestionTemplate, Principal principal) {
        String questionValue = formQuestionTemplate.getQuestionValue();
        boolean isQuestionShorterThan8Characters = checkQuestionsLength(questionValue);
        if (isQuestionShorterThan8Characters) {
            return getRedirectViewToAddQuestionPageWithErrorMessage(redirectAttributes);
        }
        UserDTO userDTO = findUser(principal);
        questionsService.save(formQuestionTemplate, userDTO);
        return new RedirectView("/");
    }

    private boolean checkQuestionsLength(String questionValue) {
        return questionValue.trim().length() < 8;
    }

    private RedirectView getRedirectViewToAddQuestionPageWithErrorMessage(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMsg", "Question must be at least 8 characters long");
        return new RedirectView("/add-question");
    }

    private UserDTO findUser(Principal principal) {
        String principalName = principal.getName();
        return usersService.findByEmail(principalName);
    }

    @GetMapping("/category/{categoryName}/questions")
    public String showQuestionsOfThisCategory(@PathVariable String categoryName, Model model, Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), 5);
        Page<QuestionDTO> questions = questionsService.findAllByCategoryIs(categoryName, pageRequest);
        model.addAttribute("questions", questions);
        List<Integer> paginationNumbers = getPaginationNumbers(model, pageable, questions);
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "questions";
    }

    private List<Integer> getPaginationNumbers(Model model, Pageable pageable, Page<QuestionDTO> questions) {
        int currentPage = pageable.getPageNumber() + 1;
        model.addAttribute("currentPage", currentPage);
        int totalPages = questions.getTotalPages();
        List<Integer> paginationNumbers;
        paginationNumbers = PaginationGenerator.createPaginationList(currentPage, totalPages);
        return paginationNumbers;
    }

    @GetMapping("/question/{questionId}")
    public String goToQuestionPage(@PathVariable Long questionId,
                                   Model model, Pageable pageable) {
        addAllNecessaryFormTemplatesToModel(model);
        QuestionDTO questionDTO = findQuestion(questionId);
        model.addAttribute("question", questionDTO);
        addCategoryToModel(questionDTO.getCategoryDTO().getName(), model);
        Page<AnswerDTO> answers = answersService.findAllByQuestionId(questionId, pageable);
        model.addAttribute("answers", answers);
        List<Integer> paginationNumbers = getPaginationNumbersList(pageable, answers);
        model.addAttribute("paginationNumbers", paginationNumbers);
        addAnswersWithCommentsToModel(model, answers);
        return "question-page";
    }

    private void addAllNecessaryFormTemplatesToModel(Model model) {
        model.addAttribute("answerForm", new AnswerDTO());
        model.addAttribute("commentForm", new CommentDTO());
        model.addAttribute("editedQuestionForm", new FormQuestionTemplate());
    }

    private QuestionDTO findQuestion(Long questionId) {
        return questionsService.findById(questionId);
    }

    private void addCategoryToModel(String categoryName, Model model) {
        CategoryDTO categoryDTO = getCategory(categoryName);
        model.addAttribute("category", categoryDTO);
    }

    private CategoryDTO getCategory(String categoryName) {
        return categoriesService.findById(categoryName);
    }

    private List<Integer> getPaginationNumbersList(Pageable pageable, Page<AnswerDTO> answers) {
        int currentPageStartingFrom1 = pageable.getPageNumber() + 1;
        int totalPages = answers.getTotalPages();
        return PaginationGenerator.createPaginationList(currentPageStartingFrom1, totalPages);
    }

    private void addAnswersWithCommentsToModel(Model model, Page<AnswerDTO> answers) {
        List<AnswerWithComments> answersWithComments = getAnswerWithCommentsList(answers);
        model.addAttribute("answersWithComments", answersWithComments);
    }

    private List<AnswerWithComments> getAnswerWithCommentsList(Page<AnswerDTO> answers) {
        List<AnswerWithComments> answersWithComments = new ArrayList<>();
        for (AnswerDTO answerDTO : answers) {
            Page<CommentDTO> comments = commentsService.findAllByAnswerId(answerDTO.getId(), PageRequest.of(0, 5));
            AnswerWithComments answerWithCommentsAdded = new AnswerWithComments(answerDTO, comments);
            answersWithComments.add(answerWithCommentsAdded);
        }
        return answersWithComments;
    }

    @PostMapping("/question/{questionId}/delete")
    public RedirectView deleteQuestion(Principal principal, @PathVariable Long questionId) {
        QuestionDTO questionDTO = findQuestion(questionId);
        if(checkIfLoggedUserIsAllowedToEditThisQuestion(principal, questionDTO)) {
            questionsService.deleteById(questionId);
            return new RedirectView("/");
        }
        throw new RuntimeException("User with email " + principal.getName() + " isn't allowed to delete question with " + questionId + " id");
    }

    private boolean checkIfLoggedUserIsAllowedToEditThisQuestion(Principal principal, QuestionDTO questionDTO) {
        return questionDTO.getUserDTO().getEmail().equals(principal.getName());
    }

    @PostMapping("/question/{questionId}/update")
    public RedirectView updateQuestion(RedirectAttributes redirectAttributes, @ModelAttribute FormQuestionTemplate formQuestionTemplate, Principal principal, @PathVariable Long questionId, HttpServletRequest httpServletRequest) {
        throwExceptionIfThereAreAnswersToEditedQuestion(questionId);
        QuestionDTO questionDTO = findQuestion(questionId);
        String questionValue = formQuestionTemplate.getQuestionValue();
        boolean isQuestionShorterThan8Characters = checkQuestionsLength(questionValue);
        if(isQuestionShorterThan8Characters) {
            return getRedirectView(redirectAttributes, httpServletRequest);
        }
        if(checkIfLoggedUserIsAllowedToEditThisQuestion(principal, questionDTO)) {
            questionsService.update(formQuestionTemplate, questionId);
            return new RedirectView(httpServletRequest.getRequestURI().replaceFirst("/update$", "") + "?" + httpServletRequest.getQueryString());
        }
        return throwExceptionUserIsNotAllowedToEditThisQuestion(principal, questionId);
    }

    private void throwExceptionIfThereAreAnswersToEditedQuestion(Long questionId) {
        boolean areThereAnswersForQuestion = !answersService.findAllByQuestionId(questionId, PageRequest.of(0, 1)).isEmpty();
        if(areThereAnswersForQuestion) {
            throw new RuntimeException();
        }
    }

    private RedirectView getRedirectView(RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
        RedirectView redirectView = new RedirectView(httpServletRequest.getRequestURI().replaceFirst("/update$", "") + "?" + httpServletRequest.getQueryString());
        redirectAttributes.addFlashAttribute("errorMsg", "Question must be at least 8 characters long");
        return redirectView;
    }

    private RedirectView throwExceptionUserIsNotAllowedToEditThisQuestion(Principal principal, Long questionId) {
        String principalName = principal.getName();
        throw new RuntimeException("User with " + principalName + " email isn't allowed to edit question with " + questionId + " id");
    }

    @GetMapping("/profile/questions")
    String seeAllQuestionsOfLoggedUser(Model model, Principal principal, Pageable pageable) {
        String userEmail = principal.getName();
        Page<QuestionDTO> questions = questionsService.findAllByUserEmailIs(userEmail, pageable);
        model.addAttribute("questions", questions);
        List<Integer> paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), questions.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "questions-of-user";
    }
}

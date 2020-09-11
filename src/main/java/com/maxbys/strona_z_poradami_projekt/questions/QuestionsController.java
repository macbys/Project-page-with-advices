package com.maxbys.strona_z_poradami_projekt.questions;

import com.maxbys.strona_z_poradami_projekt.answers.Answer;
import com.maxbys.strona_z_poradami_projekt.answers.AnswersService;
import com.maxbys.strona_z_poradami_projekt.categories.CategoriesService;
import com.maxbys.strona_z_poradami_projekt.categories.Category;
import com.maxbys.strona_z_poradami_projekt.comments.Comment;
import com.maxbys.strona_z_poradami_projekt.comments.CommentsService;
import com.maxbys.strona_z_poradami_projekt.paginagtion.PaginationGenerator;
import com.maxbys.strona_z_poradami_projekt.users.User;
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
import java.util.Optional;

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
        List<Category> categories = categoriesService.findAll();
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
        Category category = findCategory(formQuestionTemplate);
        User user = findUser(principal);
        saveQuestionToRepository(formQuestionTemplate, category, user);
        return new RedirectView("/");
    }

    private boolean checkQuestionsLength(String questionValue) {
        return questionValue.trim().length() < 8;
    }

    private RedirectView getRedirectViewToAddQuestionPageWithErrorMessage(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMsg", "Question must be at least 8 characters long");
        return new RedirectView("/add-question");
    }

    private Category findCategory(FormQuestionTemplate formQuestionTemplate) {
        String formTemplateCategory = formQuestionTemplate.getCategory();
        Optional<Category> categoryOptional = categoriesService.findById(formTemplateCategory);
        return categoryOptional.orElseThrow(() ->
                new RuntimeException("Category " + formTemplateCategory + " doesn't exist"));
    }

    private User findUser(Principal principal) {
        String principalName = principal.getName();
        Optional<User> userOptional = usersService.findByEmail(principalName);
        return userOptional.orElseThrow(() ->
                new RuntimeException("User with email " + principalName + "doesn't exist"));
    }

    private void saveQuestionToRepository(FormQuestionTemplate formQuestionTemplate, Category category, User user) {
        Question question = Question.builder()
                .value(formQuestionTemplate.getQuestionValue())
                .category(category)
                .user(user)
                .build();
        questionsService.save(question);
    }

    @GetMapping("/categories/{categoryName}/questions")
    public String showQuestionsOfThisCategory(@PathVariable String categoryName, Model model, Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), 5);
        Page<Question> questions = questionsService.findAllByCategoryIs(categoryName, pageRequest);
        model.addAttribute("questions", questions);
        List<Integer> paginationNumbers = getPaginationNumbers(model, pageable, questions);
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "questions";
    }

    private List<Integer> getPaginationNumbers(Model model, Pageable pageable, Page<Question> questions) {
        int currentPage = pageable.getPageNumber() + 1;
        model.addAttribute("currentPage", currentPage);
        int totalPages = questions.getTotalPages();
        List<Integer> paginationNumbers;
        paginationNumbers = PaginationGenerator.createPaginationList(currentPage, totalPages);
        return paginationNumbers;
    }

    @GetMapping("/categories/{categoryName}/questions/{questionId}")
    public String goToQuestionPage(@PathVariable Long questionId, @PathVariable("categoryName") String categoryId,
                                   Model model, Pageable pageable) {
        addAllNecessaryFormTemplatesToModel(model);
        Question question = findQuestion(questionId, categoryId);
        model.addAttribute("question", question);
        addCategoryToModel(categoryId, model);
        Page<Answer> answers = answersService.findAllByQuestionId(questionId, pageable);
        model.addAttribute("answers", answers);
        List<Integer> paginationNumbers = getPaginationNumbersList(pageable, answers);
        model.addAttribute("paginationNumbers", paginationNumbers);
        addAnswersWithCommentsToModel(model, answers);
        return "question-page";
    }

    private void addAllNecessaryFormTemplatesToModel(Model model) {
        model.addAttribute("answerForm", new Answer());
        model.addAttribute("commentForm", new Comment());
        model.addAttribute("editedQuestionForm", new Question());
    }

    private Question findQuestion(Long questionId, String categoryName) {
        Optional<Question> questionOptional = questionsService.findByIdAndCategoryName(questionId, categoryName);
        return questionOptional.orElseThrow(() ->
                new RuntimeException("There is no question with" + questionId + " id with " + categoryName + " category name"));
    }

    private void addCategoryToModel(String categoryName, Model model) {
        Category category = getCategory(categoryName);
        model.addAttribute("category", category);
    }

    private Category getCategory( String categoryName) {
        Optional<Category> categoryOptional = categoriesService.findById(categoryName);
        return categoryOptional.get();
    }

    private List<Integer> getPaginationNumbersList(Pageable pageable, Page<Answer> answers) {
        int currentPageStartingFrom1 = pageable.getPageNumber() + 1;
        int totalPages = answers.getTotalPages();
        return PaginationGenerator.createPaginationList(currentPageStartingFrom1, totalPages);
    }

    private void addAnswersWithCommentsToModel(Model model, Page<Answer> answers) {
        List<AnswerWithComments> answersWithComments = getAnswerWithCommentsList(answers);
        model.addAttribute("answersWithComments", answersWithComments);
    }

    private List<AnswerWithComments> getAnswerWithCommentsList(Page<Answer> answers) {
        List<AnswerWithComments> answersWithComments = new ArrayList<>();
        for (Answer answer : answers) {
            //todo sprawdzić czy to potrzebne
            answer.getUser().setPassword("");
            Page<Comment> comments = commentsService.findAllByAnswerId(answer.getId(), PageRequest.of(0, 5));
            AnswerWithComments answerWithCommentsAdded = new AnswerWithComments(answer, comments);
            answersWithComments.add(answerWithCommentsAdded);
        }
        return answersWithComments;
    }

    @PostMapping("/categories/{categoryName}/questions/{questionId}/delete")
    public RedirectView deleteQuestion(Principal principal, @PathVariable String categoryName, @PathVariable Long questionId) {
        Question question = findQuestion(questionId, categoryName);
        if(checkIfLoggedUserIsAllowedToEditThisQuestion(principal, question)) {
            questionsService.deleteById(questionId);
            return new RedirectView("/");
        }
        throw new RuntimeException("User with email " + principal.getName() + " isn't allowed to delete question with " + questionId + " id");
    }

    private boolean checkIfLoggedUserIsAllowedToEditThisQuestion(Principal principal, Question question) {
        return question.getUser().getEmail().equals(principal.getName());
    }

    @PostMapping("/categories/{categoryName}/questions/{questionId}/update")
    public RedirectView updateQuestion(RedirectAttributes redirectAttributes, @ModelAttribute Question questionForm, Principal principal, @PathVariable String categoryName, @PathVariable Long questionId, HttpServletRequest httpServletRequest) {
        throwExceptionIfThereAreAnswersToEditedQuestion(questionId);
        Question question = findQuestion(questionId, categoryName);
        String questionValue = questionForm.getValue();
        boolean isQuestionShorterThan8Characters = checkQuestionsLength(questionValue);
        if(isQuestionShorterThan8Characters) {
            return getRedirectView(redirectAttributes, httpServletRequest);
        }
        if(checkIfLoggedUserIsAllowedToEditThisQuestion(principal, question)) {
            saveQuestionToRepository(questionForm, question);
            return getRedirectViewToQuestionPage(httpServletRequest);
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
        RedirectView redirectView = getRedirectViewToQuestionPage(httpServletRequest);
        redirectAttributes.addFlashAttribute("errorMsg", "Question must be at least 8 characters long");
        return redirectView;
    }

    private RedirectView getRedirectViewToQuestionPage(HttpServletRequest httpServletRequest) {
        return new RedirectView(httpServletRequest.getRequestURI().replaceFirst("/update$", "") + "?page=0&size=5&sort=rating,desc", true);
    }

    private void saveQuestionToRepository(Question questionForm, Question question) {
        question.setValue(questionForm.getValue());
        questionsService.save(question);
    }

    private RedirectView throwExceptionUserIsNotAllowedToEditThisQuestion(Principal principal, Long questionId) {
        String principalName = principal.getName();
        throw new RuntimeException("User with " + principalName + " email isn't allowed to edit question with " + questionId + " id");
    }

    @GetMapping("/profile/questions")
    String seeAllQuestionsOfLoggedUser(Model model, Principal principal, Pageable pageable) {
        String userEmail = principal.getName();
        Page<Question> questions = questionsService.findAllByUserEmailIs(userEmail, pageable);
        model.addAttribute("questions", questions);
        List<Integer> paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), questions.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "questions-of-user";
    }
}

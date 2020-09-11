package com.maxbys.strona_z_poradami_projekt.comments;

import com.maxbys.strona_z_poradami_projekt.answers.Answer;
import com.maxbys.strona_z_poradami_projekt.answers.AnswersService;
import com.maxbys.strona_z_poradami_projekt.categories.CategoriesService;
import com.maxbys.strona_z_poradami_projekt.categories.Category;
import com.maxbys.strona_z_poradami_projekt.paginagtion.PaginationGenerator;
import com.maxbys.strona_z_poradami_projekt.questions.Question;
import com.maxbys.strona_z_poradami_projekt.questions.QuestionsService;
import com.maxbys.strona_z_poradami_projekt.questions.question_view.QuestionView;
import com.maxbys.strona_z_poradami_projekt.questions.question_view.QuestionViewRepository;
import com.maxbys.strona_z_poradami_projekt.users.User;
import com.maxbys.strona_z_poradami_projekt.users.UsersService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class CommentsController {

    private final UsersService usersService;
    private final QuestionsService questionsService;
    private final AnswersService answersService;
    private final CommentsService commentsService;
    private final CategoriesService categoriesService;
    private QuestionViewRepository questionViewRepository;

    public CommentsController(UsersService usersService, QuestionsService questionsService, AnswersService answersService, CommentsService commentsService, CategoriesService categoriesService, QuestionViewRepository questionViewRepository) {
        this.usersService = usersService;
        this.questionsService = questionsService;
        this.answersService = answersService;
        this.commentsService = commentsService;
        this.categoriesService = categoriesService;
        this.questionViewRepository = questionViewRepository;
    }

    @PostMapping("/answer/{answerId}/comments")
    public RedirectView addComment(RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest
            , @ModelAttribute Comment commentForm, Principal principal
            , @PathVariable Long answerId) {
        User user = getUser(principal);
        Answer answer = getAnswer(answerId);
        Long questionId = answer.getQuestion().getId();
        if(isCommentsLengthLowerThan8(commentForm)) {
            addErrorMessagesToRedirectAttributes(redirectAttributes, answerId);
            return new RedirectView("/question/" + questionId + "?" + httpServletRequest.getQueryString());
        }
        saveCommentToRepository(commentForm, user, answer);
        return new RedirectView("/question/" + questionId + "?" + httpServletRequest.getQueryString());
    }

    private User getUser(Principal principal) {
        String principalName = principal.getName();
        Optional<User> userOptional = usersService.findByEmail(principalName);
        return userOptional.orElseThrow(() -> new RuntimeException("User with email " + principalName + "doesn't exist"));
    }

    private Answer getAnswer(Long answerId) {
        Optional<Answer> answerOptional = answersService.findById(answerId);
        return answerOptional.orElseThrow(() ->
                new RuntimeException("There is no answer with" + answerId + " id"));
    }

    private boolean isCommentsLengthLowerThan8(Comment commentForm) {
        return commentForm.getValue().trim().length() < 8;
    }

    private void addErrorMessagesToRedirectAttributes(RedirectAttributes redirectAttributes, Long answerId) {
        redirectAttributes.addFlashAttribute("errorMsg", "Comment must be at least 8 characters long");
        redirectAttributes.addFlashAttribute("commentAnswerIdError", answerId);
    }

    private void saveCommentToRepository(Comment commentForm, User user, Answer answer) {
        Comment comment = Comment.builder()
                .answer(answer)
                .user(user)
                .value(commentForm.getValue())
                .build();
        commentsService.save(comment);
    }

    @PostMapping("/comment/{commentId}/delete")
    public RedirectView deleteComment(Principal principal
            , @PathVariable Long commentId) {
        Comment comment = getComment(commentId);
        if(checkIfUserIsAllowedToDeleteComment(principal, comment)){
            commentsService.deleteById(commentId);
            return new RedirectView("/");
        }
        throw new RuntimeException("User with email " + principal.getName() + " isn't allowed to delete comment with " + commentId + " id");
    }

    private Comment getComment(Long commentId) {
        Optional<Comment> commentOptional = commentsService.findById(commentId);
        return commentOptional.orElseThrow(() ->
                new RuntimeException("Comment with " + commentId + " id doesn't exist"));
    }

    private boolean checkIfUserIsAllowedToDeleteComment(Principal principal, Comment comment) {
        return comment.getUser().getEmail().equals(principal.getName());
    }

    @ResponseBody
    @GetMapping("/answer/{answerId}/comments")
    public Page<Comment> restGetComments(@PathVariable Long answerId, Pageable pageable) {
        Page<Comment> comments = commentsService.findAllByAnswerId(answerId, pageable);
        return comments;
    }

    @GetMapping("/profile/comments")
    String seeAllCommentsOfLoggedUser(Model model, Principal principal, Pageable pageable) {
        String userEmail = principal.getName();
        Page<Comment> answersWithLinks = commentsService.findAllByUser_Email(userEmail, pageable);
        model.addAttribute("comments", answersWithLinks);
        List<Integer> paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), answersWithLinks.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "comments-of-user";
    }

    //todo wywalić to poniżej?

    @EventListener(ApplicationReadyEvent.class)
    public void addCategories() {
        Category sport = Category.builder()
                .name("Sport")
                .build();
        categoriesService.save(sport);
        Category koszykowka = Category.builder()
                .category(sport)
                .name("koszykówka")
                .build();
        categoriesService.save(koszykowka);
        Category jedzenie = Category.builder()
                .name("Jedzenie")
                .build();
        categoriesService.save(jedzenie);
        User a = User.builder()
                .password("")
                .name("a")
                .email("")
                .build();
        usersService.save(a);
        User user = User.builder()
                .name("rolo")
                .email("maxbys@gmail.com")
                .password("redo")
                .build();
        Question question = Question.builder()
                .user(a)
                .category(sport)
                .value("jaki to kolor?")
                .build();
        usersService.save(user);
        questionsService.save(question);
        Answer answer = Answer.builder()
                .question(question)
                .value("dasfdsa")
                .rating(0)
                .user(a)
                .build();
        answersService.save(answer);
        Comment comment = Comment.builder()
                .user(a)
                .value("comment")
                .answer(answer)
                .build();
        commentsService.save(comment);
        Question question2 = questionsService.findById(1L).get();
        questionViewRepository.save(QuestionView.builder()
                .question(question2)
                .build());

        Question question3 = Question.builder()
                .user(a)
                .category(sport)
                .value("jaki to fff?")
                .build();
        questionsService.save(question3);
        QuestionView build = QuestionView.builder()
                .question(question3)
                .build();
        questionViewRepository.save(build);
        QuestionView build2 = QuestionView.builder()
                .question(question3)
                .build();
        questionViewRepository.save(build2);

        Question question4 = Question.builder()
                .user(a)
                .category(sport)
                .value("jaki to aaaa?")
                .build();
        questionsService.save(question4);
        QuestionView build3 = QuestionView.builder()
                .question(question4)
                .build();
        questionViewRepository.save(build3);
        QuestionView questionView = questionViewRepository.findById(4L).get();
        questionView.setCreationTime(Date.valueOf(LocalDate.now().minusDays(4)));
        questionViewRepository.save(questionView);
    }
}

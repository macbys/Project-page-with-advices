package com.maxbys.page_with_tips_project.comments;

import com.maxbys.page_with_tips_project.answers.AnswerDTO;
import com.maxbys.page_with_tips_project.answers.AnswersService;
import com.maxbys.page_with_tips_project.categories.CategoriesService;
import com.maxbys.page_with_tips_project.paginagtion.PaginationGenerator;
import com.maxbys.page_with_tips_project.questions.QuestionsService;
import com.maxbys.page_with_tips_project.users.UserDTO;
import com.maxbys.page_with_tips_project.users.UsersService;
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
import java.util.List;

@Controller
public class CommentsController {

    private final UsersService usersService;
    private final QuestionsService questionsService;
    private final AnswersService answersService;
    private final CommentsService commentsService;
    private final CategoriesService categoriesService;

    public CommentsController(UsersService usersService, QuestionsService questionsService, AnswersService answersService, CommentsService commentsService, CategoriesService categoriesService) {
        this.usersService = usersService;
        this.questionsService = questionsService;
        this.answersService = answersService;
        this.commentsService = commentsService;
        this.categoriesService = categoriesService;
    }

    @PostMapping("/answer/{answerId}/comments")
    public RedirectView addComment(RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest
            , @ModelAttribute CommentDTO commentDTOForm, Principal principal
            , @PathVariable Long answerId) {
        UserDTO userDTO = getUser(principal);
        AnswerDTO answerDTO = getAnswer(answerId);
        Long questionId = answerDTO.getQuestionDTO().getId();
        if(isCommentsLengthLowerThan8(commentDTOForm)) {
            addErrorMessagesToRedirectAttributes(redirectAttributes, answerId);
            return new RedirectView("/question/" + questionId + "?" + httpServletRequest.getQueryString());
        }
        saveCommentToRepository(commentDTOForm, principal.getName(), answerId);
        return new RedirectView("/question/" + questionId + "?" + httpServletRequest.getQueryString());
    }

    private UserDTO getUser(Principal principal) {
        String principalName = principal.getName();
        return usersService.findByEmail(principalName);
    }

    private AnswerDTO getAnswer(Long answerId) {
        return answersService.findById(answerId);
    }

    private boolean isCommentsLengthLowerThan8(CommentDTO commentDTOForm) {
        return commentDTOForm.getValue().trim().length() < 8;
    }

    private void addErrorMessagesToRedirectAttributes(RedirectAttributes redirectAttributes, Long answerId) {
        redirectAttributes.addFlashAttribute("errorMsg", "Comment must be at least 8 characters long");
        redirectAttributes.addFlashAttribute("commentAnswerIdError", answerId);
    }

    private void saveCommentToRepository(CommentDTO commentDTOForm, String email, Long answerId) {
        commentsService.save(commentDTOForm, answerId, email);
    }

    @PostMapping("/comment/{commentId}/delete")
    public RedirectView deleteComment(Principal principal
            , @PathVariable Long commentId) {
        CommentDTO commentDTO = getComment(commentId);
        if(checkIfUserIsAllowedToDeleteComment(principal, commentDTO)){
            commentsService.deleteById(commentId);
            return new RedirectView("/");
        }
        throw new RuntimeException("User with email " + principal.getName() + " isn't allowed to delete comment with " + commentId + " id");
    }

    private CommentDTO getComment(Long commentId) {
        return commentsService.findById(commentId);
    }

    private boolean checkIfUserIsAllowedToDeleteComment(Principal principal, CommentDTO commentDTO) {
        return commentDTO.getUserDTO().getEmail().equals(principal.getName());
    }

    @ResponseBody
    @GetMapping("/answer/{answerId}/comments")
    public Page<CommentDTO> restGetComments(@PathVariable Long answerId, Pageable pageable) {
        Page<CommentDTO> comments = commentsService.findAllByAnswerId(answerId, pageable);
        return comments;
    }

    @GetMapping("/profile/comments")
    String seeAllCommentsOfLoggedUser(Model model, Principal principal, Pageable pageable) {
        String userEmail = principal.getName();
        Page<CommentDTO> answersWithLinks = commentsService.findAllByUser_Email(userEmail, pageable);
        model.addAttribute("comments", answersWithLinks);
        List<Integer> paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), answersWithLinks.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "comments-of-user";
    }

    //todo wywalić to poniżej?

    @EventListener(ApplicationReadyEvent.class)
    public void addCategories() {

//        QuestionDTO questionDTO = QuestionDTO.builder()
//                .userDTO(a)
//                .categoryDTO(sport)
//                .value("jaki to kolor?")
//                .build();

//        questionsService.save(questionDTO);
//        AnswerDTO answerDTO = AnswerDTO.builder()
//                .questionDTO(questionDTO)
//                .value("dasfdsa")
//                .rating(0)
//                .userDTO(a)
//                .build();
//        answersService.save(answerDTO);
//        CommentDTO commentDTO = CommentDTO.builder()
//                .userDTO(a)
//                .value("comment")
//                .answerDTO(answerDTO)
//                .build();
//        commentsService.save(commentDTO);
//        QuestionDTO questionDTO2 = questionsService.findById(1L).get();
//        questionViewRepository.save(QuestionView.builder()
//                .questionDTO(questionDTO2)
//                .build());
//
//        QuestionDTO questionDTO3 = QuestionDTO.builder()
//                .userDTO(a)
//                .categoryDTO(sport)
//                .value("jaki to fff?")
//                .build();
//        questionsService.save(questionDTO3);
//        QuestionView build = QuestionView.builder()
//                .questionDTO(questionDTO3)
//                .build();
//        questionViewRepository.save(build);
//        QuestionView build2 = QuestionView.builder()
//                .questionDTO(questionDTO3)
//                .build();
//        questionViewRepository.save(build2);
//
//        QuestionDTO questionDTO4 = QuestionDTO.builder()
//                .userDTO(a)
//                .categoryDTO(sport)
//                .value("jaki to aaaa?")
//                .build();
//        questionsService.save(questionDTO4);
//        QuestionView build3 = QuestionView.builder()
//                .questionDTO(questionDTO4)
//                .build();
//        questionViewRepository.save(build3);
//        QuestionView questionView = questionViewRepository.findById(4L).get();
//        questionView.setCreationTime(Date.valueOf(LocalDate.now().minusDays(4)));
//        questionViewRepository.save(questionView);
    }
}

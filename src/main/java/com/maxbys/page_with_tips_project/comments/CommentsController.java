package com.maxbys.page_with_tips_project.comments;

import com.maxbys.page_with_tips_project.answers.AnswerDTO;
import com.maxbys.page_with_tips_project.answers.AnswersService;
import com.maxbys.page_with_tips_project.categories.CategoriesService;
import com.maxbys.page_with_tips_project.paginagtion.PaginationGenerator;
import com.maxbys.page_with_tips_project.questions.QuestionsService;
import com.maxbys.page_with_tips_project.users.UserDTO;
import com.maxbys.page_with_tips_project.users.UsersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        if(isCommentRightLength(commentDTOForm)) {
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

    private boolean isCommentRightLength(CommentDTO commentDTOForm) {
        return commentDTOForm.getValue().length() < 8 || commentDTOForm.getValue().length() > 255;
    }

    private void addErrorMessagesToRedirectAttributes(RedirectAttributes redirectAttributes, Long answerId) {
        redirectAttributes.addFlashAttribute("errorMsg", "Comment must be between 8 and 255 characters long");
        redirectAttributes.addFlashAttribute("commentAnswerIdError", answerId);
    }

    private void saveCommentToRepository(CommentDTO commentDTOForm, String email, Long answerId) {
        commentsService.save(commentDTOForm, answerId, email);
    }

    @PostMapping("/comment/{commentId}/delete")
    public RedirectView deleteComment(Authentication authentication
            , @PathVariable Long commentId) {
        CommentDTO commentDTO = getComment(commentId);
        if(checkIfUserIsAllowedToDeleteComment(authentication, commentDTO)){
            commentsService.deleteById(commentId);
            return new RedirectView("/");
        }
        throw new RuntimeException("User with email " + authentication.getName() + " isn't allowed to delete comment with " + commentId + " id");
    }

    private CommentDTO getComment(Long commentId) {
        return commentsService.findById(commentId);
    }

    private boolean checkIfUserIsAllowedToDeleteComment(Authentication authentication, CommentDTO commentDTO) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")) || commentDTO.getUserDTO().getEmail().equals(authentication.getName());
    }

    @ResponseBody
    @GetMapping("/answer/{answerId}/comments")
    public Page<CommentDTO> restGetComments(@PathVariable Long answerId, Pageable pageable) {
        Page<CommentDTO> comments = commentsService.findAllByAnswerId(answerId, pageable);
        return comments;
    }

    @GetMapping("/profile/comments")
    public String seeAllCommentsOfLoggedUser(Model model, Principal principal, Pageable pageable) {
        String userEmail = principal.getName();
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<CommentDTO> comments = commentsService.findAllByUser_Email(userEmail, pageRequest);
        model.addAttribute("comments", comments);
        List<Integer> paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), comments.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "comments-of-logged-user";
    }

    @GetMapping("/user/{userId}/comments")
    public String seeAllCommentsOfLoggedUser(Model model, Pageable pageable, @PathVariable Long userId) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<CommentDTO> comments = commentsService.findAllByUserEntity_Id(userId, pageRequest);
        model.addAttribute("comments", comments);
        UserDTO userDTO = usersService.findById(userId);
        model.addAttribute("user", userDTO);
        List<Integer> paginationNumbers = PaginationGenerator.createPaginationList(pageable.getPageNumber(), comments.getTotalPages());
        model.addAttribute("paginationNumbers", paginationNumbers);
        return "comments-of-user";
    }
}

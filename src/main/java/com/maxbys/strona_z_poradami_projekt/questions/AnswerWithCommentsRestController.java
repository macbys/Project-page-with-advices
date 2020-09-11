package com.maxbys.strona_z_poradami_projekt.questions;

import com.maxbys.strona_z_poradami_projekt.answers.Answer;
import com.maxbys.strona_z_poradami_projekt.answers.AnswersRepository;
import com.maxbys.strona_z_poradami_projekt.comments.Comment;
import com.maxbys.strona_z_poradami_projekt.comments.CommentsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AnswerWithCommentsRestController {


    private final AnswersRepository answersService;
    private final CommentsRepository commentsService;

    public AnswerWithCommentsRestController(AnswersRepository answersService, CommentsRepository commentsService) {
        this.answersService = answersService;
        this.commentsService = commentsService;
    }

    @GetMapping("questions/{questionId}")
    public List<AnswerWithComments> getAnswerWithComments(@PathVariable Long questionId, Pageable pageable) {
        List<AnswerWithComments> answersWithComments = new ArrayList<>();
        Page<Answer> answers = answersService.findAllByQuestionId(questionId, pageable);
        for (Answer answer : answers) {
            answer.getUser().setPassword("dsf");
            Page<Comment> comments = commentsService.findAllByAnswerId(answer.getId(), PageRequest.of(0, 5));
            AnswerWithComments answerWithCommentsAdded = new AnswerWithComments(answer, comments);
            answersWithComments.add(answerWithCommentsAdded);
        }
        return answersWithComments;
    }
}

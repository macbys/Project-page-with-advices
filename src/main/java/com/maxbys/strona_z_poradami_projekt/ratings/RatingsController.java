package com.maxbys.strona_z_poradami_projekt.ratings;

import com.maxbys.strona_z_poradami_projekt.answers.Answer;
import com.maxbys.strona_z_poradami_projekt.answers.AnswersService;
import com.maxbys.strona_z_poradami_projekt.users.User;
import com.maxbys.strona_z_poradami_projekt.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @PostMapping("categories/{categoryId}/questions/{questionId}/answers/{answerId}/rateUp")
    public RedirectView rateAnswerUp(Principal principal, HttpServletRequest httpServletRequest, @PathVariable Long answerId, @PathVariable String categoryId, @PathVariable String questionId) {
        RatingInfo ratingInfo = new RatingInfo(principal, answerId).invoke();
        Answer answer = ratingInfo.getAnswer();
        RatingId ratingId = ratingInfo.getRatingId();
        Optional<Rating> ratingOptional = ratingInfo.getRatingOptional();
        Integer ratingValue = ratingInfo.getRatingValue();
        if(ratingOptional.isPresent()) {
            Rating rating = ratingOptional.get();
            if(rating.getValue() == -1) {
                answer.setRating(ratingValue + 2);
                rating.setValue((byte) 1);
                ratingsService.save(rating);
            } else {
                ratingsService.deleteById(ratingId);
                answer.setRating(ratingValue - 1);
            }
        } else {
            Rating rating = new Rating(ratingId, (byte) 1);
            ratingsService.save(rating);
            answer.setRating(ratingValue + 1);
        }
        answersService.save(answer);
        String uri = Arrays.stream(httpServletRequest.getRequestURI().split("/")).limit(5).collect(Collectors.joining("/"));
        return new RedirectView( uri + "?page=0&size=5&sort=rating");
    }

    @PostMapping("categories/{categoryId}/questions/{questionId}/answers/{answerId}/rateDown")
    public RedirectView rateAnswerDown(Principal principal, HttpServletRequest httpServletRequest, @PathVariable Long answerId, @PathVariable String categoryId, @PathVariable String questionId) { 
        RatingInfo ratingInfo = new RatingInfo(principal, answerId).invoke();
        Answer answer = ratingInfo.getAnswer();
        RatingId ratingId = ratingInfo.getRatingId();
        Optional<Rating> ratingOptional = ratingInfo.getRatingOptional();
        Integer ratingValue = ratingInfo.getRatingValue();
        if(ratingOptional.isPresent()) {
            Rating rating = ratingOptional.get();
            if(rating.getValue() == 1) {
                answer.setRating(ratingValue - 2);
                rating.setValue((byte) -1);
                ratingsService.save(rating);
            } else {
                ratingsService.deleteById(ratingId);
                answer.setRating(ratingValue + 1);
            }

        } else {
            Rating rating = new Rating(ratingId, (byte) -1);
            ratingsService.save(rating);
            answer.setRating(ratingValue - 1);
        }
        answersService.save(answer);
        String uri = Arrays.stream(httpServletRequest.getRequestURI().split("/")).limit(5).collect(Collectors.joining("/"));
        return new RedirectView( uri + "?page=0&size=5&sort=rating");
    }



    private class RatingInfo {
        private Principal principal;
        private Long answerId;
        private Answer answer;
        private RatingId ratingId;
        private Optional<Rating> ratingOptional;
        private Integer ratingValue;

        public RatingInfo(Principal principal, Long answerId) {
            this.principal = principal;
            this.answerId = answerId;
        }

        public Answer getAnswer() {
            return answer;
        }

        public RatingId getRatingId() {
            return ratingId;
        }

        public Optional<Rating> getRatingOptional() {
            return ratingOptional;
        }

        public Integer getRatingValue() {
            return ratingValue;
        }

        public RatingInfo invoke() {
            Optional<Answer> answerOptional = answersService.findById(answerId);
            answer = answerOptional.orElseThrow(() -> new RuntimeException());
            Optional<User> userOptional = usersService.findByEmail(principal.getName());
            User user = userOptional.orElseThrow(() -> new RuntimeException());
            ratingId = new RatingId(user, answer);
            ratingOptional = ratingsService.findyById(ratingId);
            ratingValue = answer.getRating();
            return this;
        }
    }
}

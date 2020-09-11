package com.maxbys.strona_z_poradami_projekt.ratings;

import com.maxbys.strona_z_poradami_projekt.answers.Answer;
import com.maxbys.strona_z_poradami_projekt.answers.AnswersService;
import com.maxbys.strona_z_poradami_projekt.questions.Question;
import com.maxbys.strona_z_poradami_projekt.users.User;
import com.maxbys.strona_z_poradami_projekt.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;

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
        RatingInfo ratingInfo = new RatingInfo(principal, answerId).invoke();
        Optional<Rating> ratingOptional = ratingInfo.getRatingOptional();
        if(ratingOptional.isPresent()) {
            setUpRatingUpIfExist(ratingInfo, ratingOptional);
        } else {
            setUpRatingUpIfDoesntExist(ratingInfo);
        }
        return getRedirectViewToQuestionPage(httpServletRequest, ratingInfo);
    }

    private void setUpRatingUpIfExist(RatingInfo ratingInfo, Optional<Rating> ratingOptional) {
        Rating rating = ratingOptional.get();
        Integer ratingValue = ratingInfo.getRatingValue();
        Answer answer = ratingInfo.getAnswer();
        if(rating.getValue() == -1) {
            answer.setRating(ratingValue + 2);
            saveRatingWithValue(rating, 1);
        } else {
            RatingId ratingId = rating.getRatingId();
            ratingsService.deleteById(ratingId);
            answer.setRating(ratingValue - 1);
        }
    }

    private void saveRatingWithValue(Rating rating, int value) {
        rating.setValue((byte) value);
        ratingsService.save(rating);
    }

    private void setUpRatingUpIfDoesntExist(RatingInfo ratingInfo) {
        RatingId ratingId = ratingInfo.getRatingId();
        Rating rating = new Rating(ratingId, (byte) 1);
        ratingsService.save(rating);
        Integer ratingValue = ratingInfo.getRatingValue();
        ratingInfo.getAnswer().setRating(ratingValue + 1);
    }

    private RedirectView getRedirectViewToQuestionPage(HttpServletRequest httpServletRequest, RatingInfo ratingInfo) {
        Answer answer = ratingInfo.getAnswer();
        answersService.save(answer);
        Question question = answer.getQuestion();
        Long questionId = question.getId();
        return new RedirectView("/question/" + questionId + "?" + httpServletRequest.getQueryString());
    }

    @PostMapping("/answer/{answerId}/rateDown")
    public RedirectView rateAnswerDown(Principal principal, HttpServletRequest httpServletRequest, @PathVariable Long answerId) {
        RatingInfo ratingInfo = new RatingInfo(principal, answerId).invoke();
        Optional<Rating> ratingOptional = ratingInfo.getRatingOptional();
        if(ratingOptional.isPresent()) {
            setDownRatingUpIfExist(ratingInfo, ratingOptional);
        } else {
            setUpRatingDownIfDoesntExist(ratingInfo);
        }
        return getRedirectViewToQuestionPage(httpServletRequest, ratingInfo);
    }

    private void setDownRatingUpIfExist(RatingInfo ratingInfo, Optional<Rating> ratingOptional) {
        Rating rating = ratingOptional.get();
        Integer ratingValue = ratingInfo.getRatingValue();
        Answer answer = ratingInfo.getAnswer();
        if(rating.getValue() == 1) {
            answer.setRating(ratingValue - 2);
            saveRatingWithValue(rating, -1);
        } else {
            RatingId ratingId = rating.getRatingId();
            ratingsService.deleteById(ratingId);
            answer.setRating(ratingValue + 1);
        }
    }

    private void setUpRatingDownIfDoesntExist(RatingInfo ratingInfo) {
        RatingId ratingId = ratingInfo.getRatingId();
        Rating rating = new Rating(ratingId, (byte) -1);
        ratingsService.save(rating);
        Integer ratingValue = ratingInfo.getRatingValue();
        ratingInfo.getAnswer().setRating(ratingValue - 1);
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

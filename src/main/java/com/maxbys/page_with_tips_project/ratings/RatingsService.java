package com.maxbys.page_with_tips_project.ratings;

import com.maxbys.page_with_tips_project.answers.AnswerEntity;
import com.maxbys.page_with_tips_project.answers.AnswersRepository;
import com.maxbys.page_with_tips_project.users.UserEntity;
import com.maxbys.page_with_tips_project.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RatingsService {

    private final RatingsRepository ratingsRepository;
    private final UsersRepository usersRepository;
    private final AnswersRepository answersRepository;

    @Autowired
    public RatingsService(RatingsRepository ratingsRepository, UsersRepository usersRepository, AnswersRepository answersRepository) {
        this.ratingsRepository = ratingsRepository;
        this.usersRepository = usersRepository;
        this.answersRepository = answersRepository;
    }

    public void addRateUp(String email, Long answerId) {
        UserAndAnswerDataReceiver userAndAnswerDataReceiver = new UserAndAnswerDataReceiver(email, answerId).invoke();
        if(userAndAnswerDataReceiver.getRatingEntityOptional().isPresent()) {
            addRateUpWhenItExists(userAndAnswerDataReceiver);
        } else {
            addRateUpWhenItDoesNotExist(userAndAnswerDataReceiver);
        }
        answersRepository.save(userAndAnswerDataReceiver.getAnswerEntity());
    }

    private void addRateUpWhenItExists(UserAndAnswerDataReceiver userAndAnswerDataReceiver) {
        if(checkIfExistingRatingHasValueMinusOne(userAndAnswerDataReceiver)) {
            changeExistingRatingToValueOne(userAndAnswerDataReceiver);
        } else {
            deleteExistingRatingAndLowerRatingByOne(userAndAnswerDataReceiver);
        }
    }

    private boolean checkIfExistingRatingHasValueMinusOne(UserAndAnswerDataReceiver userAndAnswerDataReceiver) {
        Optional<RatingEntity> ratingEntityOptional = userAndAnswerDataReceiver.getRatingEntityOptional();
        RatingEntity ratingEntity = ratingEntityOptional.get();
        Byte ratingValue = ratingEntity.getValue();
        return ratingValue == -1;
    }

    private void changeExistingRatingToValueOne(UserAndAnswerDataReceiver userAndAnswerDataReceiver) {
        AnswerEntity answerEntity = userAndAnswerDataReceiver.getAnswerEntity();
        Integer answerRating = answerEntity.getRating();
        answerEntity.setRating(answerRating + 2);
        Optional<RatingEntity> ratingEntityOptional = userAndAnswerDataReceiver.getRatingEntityOptional();
        RatingEntity ratingEntity = ratingEntityOptional.get();
        ratingEntity.setValue((byte) 1);
        ratingsRepository.save(ratingEntity);
    }

    private void deleteExistingRatingAndLowerRatingByOne(UserAndAnswerDataReceiver userAndAnswerDataReceiver) {
        UserEntity userEntity = userAndAnswerDataReceiver.getUserEntity();
        AnswerEntity answerEntity = userAndAnswerDataReceiver.getAnswerEntity();
        RatingId ratingId = new RatingId(userEntity, answerEntity);
        ratingsRepository.deleteById(ratingId);
        userAndAnswerDataReceiver.getAnswerEntity().setRating(userAndAnswerDataReceiver.getAnswerEntity().getRating() - 1);
    }

    private void addRateUpWhenItDoesNotExist(UserAndAnswerDataReceiver userAndAnswerDataReceiver) {
        UserEntity userEntity = userAndAnswerDataReceiver.getUserEntity();
        AnswerEntity answerEntity = userAndAnswerDataReceiver.getAnswerEntity();
        RatingEntity ratingEntity = RatingEntity.apply((byte) 1, userEntity, answerEntity);
        ratingsRepository.save(ratingEntity);
        Integer rating = answerEntity.getRating();
        answerEntity.setRating(rating + 1);
    }

    public void addRateDown(String email, Long answerId) {
        UserAndAnswerDataReceiver userAndAnswerDataReceiver = new UserAndAnswerDataReceiver(email, answerId).invoke();
        if(userAndAnswerDataReceiver.getRatingEntityOptional().isPresent()) {
            addRateDownWhenItExists(userAndAnswerDataReceiver);
        } else {
            addRateDownWhenItDoesNotExist(userAndAnswerDataReceiver);
        }
        answersRepository.save(userAndAnswerDataReceiver.getAnswerEntity());
    }

    private void addRateDownWhenItExists(UserAndAnswerDataReceiver userAndAnswerDataReceiver) {
        if(checkIfExistingRatingHasValueOne(userAndAnswerDataReceiver)) {
            changeExistingRatingToValueMinusOne(userAndAnswerDataReceiver);
        } else {
            deleteExistingRatingAndIncreaseRatingByOne(userAndAnswerDataReceiver);
        }
    }

    private boolean checkIfExistingRatingHasValueOne(UserAndAnswerDataReceiver userAndAnswerDataReceiver) {
        Optional<RatingEntity> ratingEntityOptional = userAndAnswerDataReceiver.getRatingEntityOptional();
        RatingEntity ratingEntity = ratingEntityOptional.get();
        Byte ratingValue = ratingEntity.getValue();
        return ratingValue == 1;
    }

    private void changeExistingRatingToValueMinusOne(UserAndAnswerDataReceiver userAndAnswerDataReceiver) {
        AnswerEntity answerEntity = userAndAnswerDataReceiver.getAnswerEntity();
        Integer answerRating = answerEntity.getRating();
        answerEntity.setRating(answerRating - 2);
        Optional<RatingEntity> ratingEntityOptional = userAndAnswerDataReceiver.getRatingEntityOptional();
        RatingEntity ratingEntity = ratingEntityOptional.get();
        ratingEntity.setValue((byte) -1);
        ratingsRepository.save(ratingEntity);
    }

    private void deleteExistingRatingAndIncreaseRatingByOne(UserAndAnswerDataReceiver userAndAnswerDataReceiver) {
        UserEntity userEntity = userAndAnswerDataReceiver.getUserEntity();
        AnswerEntity answerEntity = userAndAnswerDataReceiver.getAnswerEntity();
        RatingId ratingId = new RatingId(userEntity, answerEntity);
        ratingsRepository.deleteById(ratingId);
        userAndAnswerDataReceiver.getAnswerEntity().setRating(userAndAnswerDataReceiver.getAnswerEntity().getRating() + 1);
    }

    private void addRateDownWhenItDoesNotExist(UserAndAnswerDataReceiver userAndAnswerDataReceiver) {
        UserEntity userEntity = userAndAnswerDataReceiver.getUserEntity();
        AnswerEntity answerEntity = userAndAnswerDataReceiver.getAnswerEntity();
        RatingEntity ratingEntity = RatingEntity.apply((byte) -1, userEntity, answerEntity);
        ratingsRepository.save(ratingEntity);
        Integer rating = answerEntity.getRating();
        answerEntity.setRating(rating - 1);
    }

    private class UserAndAnswerDataReceiver {
        private final String email;
        private final Long answerId;
        private UserEntity userEntity;
        private AnswerEntity answerEntity;
        private Optional<RatingEntity> ratingEntityOptional;

        public UserAndAnswerDataReceiver(String email, Long answerId) {
            this.email = email;
            this.answerId = answerId;
        }

        public UserEntity getUserEntity() {
            return userEntity;
        }

        public AnswerEntity getAnswerEntity() {
            return answerEntity;
        }

        public Optional<RatingEntity> getRatingEntityOptional() {
            return ratingEntityOptional;
        }

        public UserAndAnswerDataReceiver invoke() {
            Optional<UserEntity> userEntityOptional = usersRepository.findByEmail(email);
            userEntity = userEntityOptional.orElseThrow(() ->
                    new RuntimeException("User with email " + email + " doesn't exist"));
            Optional<AnswerEntity> answerEntityOptional = answersRepository.findById(answerId);
            answerEntity = answerEntityOptional.orElseThrow(() ->
                    new RuntimeException("Answer with id " + answerId + " doesn't exist"));
            ratingEntityOptional = ratingsRepository.findById(new RatingId(userEntity, answerEntity));
            return this;
        }
    }
}
